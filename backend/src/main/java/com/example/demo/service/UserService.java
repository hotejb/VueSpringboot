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
                .accountLocked(user.getStatus() != User.UserStatus.ACTIVE)
                .credentialsExpired(false)
                .disabled(user.getStatus() == User.UserStatus.INACTIVE)
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
} 