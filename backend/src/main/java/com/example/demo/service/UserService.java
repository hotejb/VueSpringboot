package com.example.demo.service;

import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class UserService implements UserDetailsService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (!userOpt.isPresent()) {
            throw new UsernameNotFoundException("用户不存在: " + username);
        }
        
        User user = userOpt.get();
        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .authorities(Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRole().name())))
                .accountExpired(false)
                .accountLocked(false) // 不锁定账户，让JWT认证处理权限
                .credentialsExpired(false)
                .disabled(false) // 不禁用账户，让JWT认证处理权限
                .build();
    }
    
    public boolean authenticate(String username, String password) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            return passwordEncoder.matches(password, user.getPassword());
        }
        return false;
    }
    
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
    
    public User createUser(String username, String password, String email, String fullName) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setEmail(email);
        user.setFullName(fullName);
        return userRepository.save(user);
    }
    
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }
    
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }
    
    // 分页获取所有用户
    public Page<User> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable);
    }
    
    // 搜索用户
    public Page<User> searchUsers(String search, Pageable pageable) {
        return userRepository.findByUsernameContainingOrFullNameContainingOrEmailContaining(
            search, search, search, pageable);
    }
    
    // 带过滤条件的搜索用户
    public Page<User> searchUsersWithFilters(String search, String status, String role, Pageable pageable) {
        User.UserStatus userStatus = null;
        User.UserRole userRole = null;
        
        // 解析状态参数
        if (status != null && !status.trim().isEmpty()) {
            try {
                userStatus = User.UserStatus.valueOf(status.toUpperCase());
            } catch (IllegalArgumentException e) {
                // 忽略无效的状态值
            }
        }
        
        // 解析角色参数
        if (role != null && !role.trim().isEmpty()) {
            try {
                userRole = User.UserRole.valueOf(role.toUpperCase());
            } catch (IllegalArgumentException e) {
                // 忽略无效的角色值
            }
        }
        
        // 根据不同的过滤条件组合调用相应的查询方法
        boolean hasSearch = search != null && !search.trim().isEmpty();
        boolean hasStatus = userStatus != null;
        boolean hasRole = userRole != null;
        
        if (hasSearch && hasStatus && hasRole) {
            // 搜索文本 + 状态 + 角色
            return userRepository.findByStatusAndRoleAndUsernameContainingOrStatusAndRoleAndFullNameContainingOrStatusAndRoleAndEmailContaining(
                userStatus, userRole, search,
                userStatus, userRole, search,
                userStatus, userRole, search,
                pageable);
        } else if (hasSearch && hasStatus) {
            // 搜索文本 + 状态
            return userRepository.findByStatusAndUsernameContainingOrStatusAndFullNameContainingOrStatusAndEmailContaining(
                userStatus, search,
                userStatus, search,
                userStatus, search,
                pageable);
        } else if (hasSearch && hasRole) {
            // 搜索文本 + 角色
            return userRepository.findByRoleAndUsernameContainingOrRoleAndFullNameContainingOrRoleAndEmailContaining(
                userRole, search,
                userRole, search,
                userRole, search,
                pageable);
        } else if (hasStatus && hasRole) {
            // 状态 + 角色
            return userRepository.findByStatusAndRole(userStatus, userRole, pageable);
        } else if (hasSearch) {
            // 仅搜索文本
            return userRepository.findByUsernameContainingOrFullNameContainingOrEmailContaining(
                search, search, search, pageable);
        } else if (hasStatus) {
            // 仅状态
            return userRepository.findByStatus(userStatus, pageable);
        } else if (hasRole) {
            // 仅角色
            return userRepository.findByRole(userRole, pageable);
        } else {
            // 无过滤条件，返回所有用户
            return userRepository.findAll(pageable);
        }
    }
    
    // 根据ID查找用户
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }
    
    // 创建用户（重载方法）
    public User createUser(User user) {
        // 检查用户名和邮箱是否已存在
        if (existsByUsername(user.getUsername())) {
            throw new RuntimeException("用户名已存在");
        }
        if (existsByEmail(user.getEmail())) {
            throw new RuntimeException("邮箱已存在");
        }
        
        // 加密密码
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        
        // 设置默认值
        if (user.getRole() == null) {
            user.setRole(User.UserRole.USER);
        }
        if (user.getStatus() == null) {
            user.setStatus(User.UserStatus.ACTIVE);
        }
        
        return userRepository.save(user);
    }
    
    // 更新用户
    public User updateUser(User user) {
        Optional<User> existingUserOpt = userRepository.findById(user.getId());
        if (!existingUserOpt.isPresent()) {
            throw new RuntimeException("用户不存在");
        }
        
        User existingUser = existingUserOpt.get();
        
        // 检查用户名和邮箱是否被其他用户使用
        Optional<User> userWithSameUsername = userRepository.findByUsername(user.getUsername());
        if (userWithSameUsername.isPresent() && !userWithSameUsername.get().getId().equals(user.getId())) {
            throw new RuntimeException("用户名已被其他用户使用");
        }
        
        Optional<User> userWithSameEmail = userRepository.findByEmail(user.getEmail());
        if (userWithSameEmail.isPresent() && !userWithSameEmail.get().getId().equals(user.getId())) {
            throw new RuntimeException("邮箱已被其他用户使用");
        }
        
        // 更新字段（不更新密码，除非明确指定）
        existingUser.setUsername(user.getUsername());
        existingUser.setEmail(user.getEmail());
        existingUser.setFullName(user.getFullName());
        existingUser.setPhone(user.getPhone());
        existingUser.setDepartment(user.getDepartment());
        existingUser.setPosition(user.getPosition());
        existingUser.setRole(user.getRole());
        existingUser.setStatus(user.getStatus());
        
        return userRepository.save(existingUser);
    }
    
    // 删除用户
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("用户不存在");
        }
        userRepository.deleteById(id);
    }
    
    // 批量删除用户
    public void deleteUsers(List<Long> ids) {
        userRepository.deleteAllById(ids);
    }
    
    // 更新用户状态
    public User updateUserStatus(Long id, User.UserStatus status) {
        Optional<User> userOpt = userRepository.findById(id);
        if (!userOpt.isPresent()) {
            throw new RuntimeException("用户不存在");
        }
        
        User user = userOpt.get();
        user.setStatus(status);
        return userRepository.save(user);
    }
    
    // 重置密码
    public void resetPassword(Long id, String newPassword) {
        Optional<User> userOpt = userRepository.findById(id);
        if (!userOpt.isPresent()) {
            throw new RuntimeException("用户不存在");
        }
        
        User user = userOpt.get();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }
    
    // 更新最后登录时间
    public void updateLastLogin(String username) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setLastLogin(LocalDateTime.now());
            userRepository.save(user);
        }
    }
    
    // 批量导入用户
    public List<String> importUsers(List<User> users) {
        List<String> errors = new ArrayList<>();
        List<User> validUsers = new ArrayList<>();
        
        for (int i = 0; i < users.size(); i++) {
            User user = users.get(i);
            int rowNum = i + 2; // Excel行号（从第2行开始）
            
            try {
                // 验证必填字段
                if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
                    errors.add("第" + rowNum + "行：用户名不能为空");
                    continue;
                }
                if (user.getFullName() == null || user.getFullName().trim().isEmpty()) {
                    errors.add("第" + rowNum + "行：姓名不能为空");
                    continue;
                }
                if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
                    errors.add("第" + rowNum + "行：邮箱不能为空");
                    continue;
                }
                
                // 检查用户名是否已存在
                if (existsByUsername(user.getUsername())) {
                    errors.add("第" + rowNum + "行：用户名 '" + user.getUsername() + "' 已存在");
                    continue;
                }
                
                // 检查邮箱是否已存在
                if (existsByEmail(user.getEmail())) {
                    errors.add("第" + rowNum + "行：邮箱 '" + user.getEmail() + "' 已存在");
                    continue;
                }
                
                // 加密密码
                user.setPassword(passwordEncoder.encode(user.getPassword()));
                
                validUsers.add(user);
                
            } catch (Exception e) {
                errors.add("第" + rowNum + "行：数据格式错误 - " + e.getMessage());
            }
        }
        
        // 批量保存有效用户
        if (!validUsers.isEmpty()) {
            try {
                userRepository.saveAll(validUsers);
            } catch (Exception e) {
                errors.add("批量保存失败：" + e.getMessage());
            }
        }
        
        return errors;
    }
    
    // 获取所有用户（不分页，用于导出）
    public List<User> getAllUsersForExport() {
        return userRepository.findAll();
    }
} 