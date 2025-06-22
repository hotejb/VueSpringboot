package com.example.demo.service;

import com.example.demo.entity.Role;
import com.example.demo.entity.Permission;
import com.example.demo.repository.RoleRepository;
import com.example.demo.repository.PermissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@Transactional
public class RoleService {
    
    @Autowired
    private RoleRepository roleRepository;
    
    @Autowired
    private PermissionRepository permissionRepository;
    
    // 分页获取所有角色
    public Page<Role> getAllRoles(Pageable pageable) {
        return roleRepository.findAll(pageable);
    }
    
    // 搜索角色
    public Page<Role> searchRoles(String search, Pageable pageable) {
        return roleRepository.findByNameContainingOrCodeContaining(search, search, pageable);
    }
    
    // 带过滤条件的搜索角色
    public Page<Role> searchRolesWithFilters(String search, String status, Pageable pageable) {
        Role.RoleStatus roleStatus = null;
        
        // 解析状态参数
        if (status != null && !status.trim().isEmpty()) {
            try {
                roleStatus = Role.RoleStatus.valueOf(status.toUpperCase());
            } catch (IllegalArgumentException e) {
                // 忽略无效的状态值
            }
        }
        
        // 根据不同的过滤条件组合调用相应的查询方法
        boolean hasSearch = search != null && !search.trim().isEmpty();
        boolean hasStatus = roleStatus != null;
        
        if (hasSearch && hasStatus) {
            // 搜索文本 + 状态
            return roleRepository.findByStatusAndNameContainingOrStatusAndCodeContaining(
                roleStatus, search, roleStatus, search, pageable);
        } else if (hasSearch) {
            // 仅搜索文本
            return roleRepository.findByNameContainingOrCodeContaining(search, search, pageable);
        } else if (hasStatus) {
            // 仅状态
            return roleRepository.findByStatus(roleStatus, pageable);
        } else {
            // 无过滤条件，返回所有角色
            return roleRepository.findAll(pageable);
        }
    }
    
    // 根据ID查找角色
    public Optional<Role> findById(Long id) {
        return roleRepository.findById(id);
    }
    
    // 根据ID查找角色（包含权限）
    public Optional<Role> findByIdWithPermissions(Long id) {
        return roleRepository.findByIdWithPermissions(id);
    }
    
    // 根据名称查找角色
    public Optional<Role> findByName(String name) {
        return roleRepository.findByName(name);
    }
    
    // 根据代码查找角色
    public Optional<Role> findByCode(String code) {
        return roleRepository.findByCode(code);
    }
    
    // 检查角色名称是否存在
    public boolean existsByName(String name) {
        return roleRepository.existsByName(name);
    }
    
    // 检查角色代码是否存在
    public boolean existsByCode(String code) {
        return roleRepository.existsByCode(code);
    }
    
    // 创建角色
    public Role createRole(Role role) {
        // 检查角色名称和代码是否已存在
        if (existsByName(role.getName())) {
            throw new RuntimeException("角色名称已存在");
        }
        if (existsByCode(role.getCode())) {
            throw new RuntimeException("角色代码已存在");
        }
        
        // 设置默认值
        if (role.getStatus() == null) {
            role.setStatus(Role.RoleStatus.ACTIVE);
        }
        if (role.getSortOrder() == null) {
            role.setSortOrder(0);
        }
        
        return roleRepository.save(role);
    }
    
    // 更新角色
    public Role updateRole(Role role) {
        Optional<Role> existingRoleOpt = roleRepository.findById(role.getId());
        if (!existingRoleOpt.isPresent()) {
            throw new RuntimeException("角色不存在");
        }
        
        Role existingRole = existingRoleOpt.get();
        
        // 检查角色名称和代码是否被其他角色使用
        Optional<Role> roleWithSameName = roleRepository.findByName(role.getName());
        if (roleWithSameName.isPresent() && !roleWithSameName.get().getId().equals(role.getId())) {
            throw new RuntimeException("角色名称已被其他角色使用");
        }
        
        Optional<Role> roleWithSameCode = roleRepository.findByCode(role.getCode());
        if (roleWithSameCode.isPresent() && !roleWithSameCode.get().getId().equals(role.getId())) {
            throw new RuntimeException("角色代码已被其他角色使用");
        }
        
        // 更新字段
        existingRole.setName(role.getName());
        existingRole.setCode(role.getCode());
        existingRole.setDescription(role.getDescription());
        existingRole.setStatus(role.getStatus());
        existingRole.setSortOrder(role.getSortOrder());
        
        return roleRepository.save(existingRole);
    }
    
    // 删除角色
    public void deleteRole(Long id) {
        if (!roleRepository.existsById(id)) {
            throw new RuntimeException("角色不存在");
        }
        roleRepository.deleteById(id);
    }
    
    // 批量删除角色
    public void deleteRoles(List<Long> ids) {
        roleRepository.deleteAllById(ids);
    }
    
    // 更新角色状态
    public Role updateRoleStatus(Long id, Role.RoleStatus status) {
        Optional<Role> roleOpt = roleRepository.findById(id);
        if (!roleOpt.isPresent()) {
            throw new RuntimeException("角色不存在");
        }
        
        Role role = roleOpt.get();
        role.setStatus(status);
        return roleRepository.save(role);
    }
    
    // 获取所有启用的角色
    public List<Role> getActiveRoles() {
        return roleRepository.findByStatusOrderBySortOrderAsc(Role.RoleStatus.ACTIVE);
    }
    
    // 为角色分配权限
    public Role assignPermissions(Long roleId, Set<Long> permissionIds) {
        Optional<Role> roleOpt = roleRepository.findByIdWithPermissions(roleId);
        if (!roleOpt.isPresent()) {
            throw new RuntimeException("角色不存在");
        }
        
        Role role = roleOpt.get();
        
        // 清空现有权限
        role.getPermissions().clear();
        
        // 添加新权限
        if (permissionIds != null && !permissionIds.isEmpty()) {
            List<Permission> permissions = permissionRepository.findAllById(permissionIds);
            role.getPermissions().addAll(permissions);
        }
        
        return roleRepository.save(role);
    }
    
    // 为角色添加权限
    public Role addPermissions(Long roleId, Set<Long> permissionIds) {
        Optional<Role> roleOpt = roleRepository.findByIdWithPermissions(roleId);
        if (!roleOpt.isPresent()) {
            throw new RuntimeException("角色不存在");
        }
        
        Role role = roleOpt.get();
        
        // 添加新权限
        if (permissionIds != null && !permissionIds.isEmpty()) {
            List<Permission> permissions = permissionRepository.findAllById(permissionIds);
            role.getPermissions().addAll(permissions);
        }
        
        return roleRepository.save(role);
    }
    
    // 为角色移除权限
    public Role removePermissions(Long roleId, Set<Long> permissionIds) {
        Optional<Role> roleOpt = roleRepository.findByIdWithPermissions(roleId);
        if (!roleOpt.isPresent()) {
            throw new RuntimeException("角色不存在");
        }
        
        Role role = roleOpt.get();
        
        // 移除权限
        if (permissionIds != null && !permissionIds.isEmpty()) {
            List<Permission> permissions = permissionRepository.findAllById(permissionIds);
            role.getPermissions().removeAll(permissions);
        }
        
        return roleRepository.save(role);
    }
    
    // 获取所有角色及其权限
    public List<Role> getAllRolesWithPermissions() {
        return roleRepository.findAllWithPermissions();
    }
} 