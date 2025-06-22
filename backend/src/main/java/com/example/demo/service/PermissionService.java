package com.example.demo.service;

import com.example.demo.entity.Permission;
import com.example.demo.repository.PermissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class PermissionService {
    
    @Autowired
    private PermissionRepository permissionRepository;
    
    // 分页获取所有权限
    public Page<Permission> getAllPermissions(Pageable pageable) {
        return permissionRepository.findAll(pageable);
    }
    
    // 搜索权限
    public Page<Permission> searchPermissions(String search, Pageable pageable) {
        return permissionRepository.findByNameContainingOrCodeContainingOrModuleContaining(
            search, search, search, pageable);
    }
    
    // 带过滤条件的搜索权限
    public Page<Permission> searchPermissionsWithFilters(String search, String status, String module, String type, Pageable pageable) {
        Permission.PermissionStatus permissionStatus = null;
        Permission.PermissionType permissionType = null;
        
        // 解析状态参数
        if (status != null && !status.trim().isEmpty()) {
            try {
                permissionStatus = Permission.PermissionStatus.valueOf(status.toUpperCase());
            } catch (IllegalArgumentException e) {
                // 忽略无效的状态值
            }
        }
        
        // 解析类型参数
        if (type != null && !type.trim().isEmpty()) {
            try {
                permissionType = Permission.PermissionType.valueOf(type.toUpperCase());
            } catch (IllegalArgumentException e) {
                // 忽略无效的类型值
            }
        }
        
        // 根据不同的过滤条件组合调用相应的查询方法
        boolean hasSearch = search != null && !search.trim().isEmpty();
        boolean hasStatus = permissionStatus != null;
        boolean hasModule = module != null && !module.trim().isEmpty();
        boolean hasType = permissionType != null;
        
        if (hasSearch && hasStatus) {
            // 搜索文本 + 状态
            return permissionRepository.findByStatusAndNameContainingOrStatusAndCodeContainingOrStatusAndModuleContaining(
                permissionStatus, search, permissionStatus, search, permissionStatus, search, pageable);
        } else if (hasSearch && hasModule) {
            // 搜索文本 + 模块
            return permissionRepository.findByModuleAndNameContainingOrModuleAndCodeContaining(
                module, search, module, search, pageable);
        } else if (hasSearch) {
            // 仅搜索文本
            return permissionRepository.findByNameContainingOrCodeContainingOrModuleContaining(
                search, search, search, pageable);
        } else if (hasStatus) {
            // 仅状态
            return permissionRepository.findByStatus(permissionStatus, pageable);
        } else if (hasModule) {
            // 仅模块
            return permissionRepository.findByModule(module, pageable);
        } else if (hasType) {
            // 仅类型
            return permissionRepository.findByType(permissionType, pageable);
        } else {
            // 无过滤条件，返回所有权限
            return permissionRepository.findAll(pageable);
        }
    }
    
    // 根据ID查找权限
    public Optional<Permission> findById(Long id) {
        return permissionRepository.findById(id);
    }
    
    // 根据名称查找权限
    public Optional<Permission> findByName(String name) {
        return permissionRepository.findByName(name);
    }
    
    // 根据代码查找权限
    public Optional<Permission> findByCode(String code) {
        return permissionRepository.findByCode(code);
    }
    
    // 检查权限名称是否存在
    public boolean existsByName(String name) {
        return permissionRepository.existsByName(name);
    }
    
    // 检查权限代码是否存在
    public boolean existsByCode(String code) {
        return permissionRepository.existsByCode(code);
    }
    
    // 创建权限
    public Permission createPermission(Permission permission) {
        // 检查权限名称和代码是否已存在
        if (existsByName(permission.getName())) {
            throw new RuntimeException("权限名称已存在");
        }
        if (existsByCode(permission.getCode())) {
            throw new RuntimeException("权限代码已存在");
        }
        
        // 设置默认值
        if (permission.getStatus() == null) {
            permission.setStatus(Permission.PermissionStatus.ACTIVE);
        }
        if (permission.getType() == null) {
            permission.setType(Permission.PermissionType.MENU);
        }
        if (permission.getSortOrder() == null) {
            permission.setSortOrder(0);
        }
        
        return permissionRepository.save(permission);
    }
    
    // 更新权限
    public Permission updatePermission(Permission permission) {
        Optional<Permission> existingPermissionOpt = permissionRepository.findById(permission.getId());
        if (!existingPermissionOpt.isPresent()) {
            throw new RuntimeException("权限不存在");
        }
        
        Permission existingPermission = existingPermissionOpt.get();
        
        // 检查权限名称和代码是否被其他权限使用
        Optional<Permission> permissionWithSameName = permissionRepository.findByName(permission.getName());
        if (permissionWithSameName.isPresent() && !permissionWithSameName.get().getId().equals(permission.getId())) {
            throw new RuntimeException("权限名称已被其他权限使用");
        }
        
        Optional<Permission> permissionWithSameCode = permissionRepository.findByCode(permission.getCode());
        if (permissionWithSameCode.isPresent() && !permissionWithSameCode.get().getId().equals(permission.getId())) {
            throw new RuntimeException("权限代码已被其他权限使用");
        }
        
        // 更新字段
        existingPermission.setName(permission.getName());
        existingPermission.setCode(permission.getCode());
        existingPermission.setDescription(permission.getDescription());
        existingPermission.setModule(permission.getModule());
        existingPermission.setType(permission.getType());
        existingPermission.setStatus(permission.getStatus());
        existingPermission.setSortOrder(permission.getSortOrder());
        
        return permissionRepository.save(existingPermission);
    }
    
    // 删除权限
    public void deletePermission(Long id) {
        if (!permissionRepository.existsById(id)) {
            throw new RuntimeException("权限不存在");
        }
        permissionRepository.deleteById(id);
    }
    
    // 批量删除权限
    public void deletePermissions(List<Long> ids) {
        permissionRepository.deleteAllById(ids);
    }
    
    // 更新权限状态
    public Permission updatePermissionStatus(Long id, Permission.PermissionStatus status) {
        Optional<Permission> permissionOpt = permissionRepository.findById(id);
        if (!permissionOpt.isPresent()) {
            throw new RuntimeException("权限不存在");
        }
        
        Permission permission = permissionOpt.get();
        permission.setStatus(status);
        return permissionRepository.save(permission);
    }
    
    // 获取所有启用的权限
    public List<Permission> getActivePermissions() {
        return permissionRepository.findByStatusOrderByModuleAscSortOrderAsc(Permission.PermissionStatus.ACTIVE);
    }
    
    // 按模块获取启用的权限
    public List<Permission> getActivePermissionsByModule(String module) {
        return permissionRepository.findByModuleAndStatusOrderBySortOrderAsc(module, Permission.PermissionStatus.ACTIVE);
    }
    
    // 获取所有模块名称
    public List<String> getAllModules() {
        return permissionRepository.findDistinctModuleByStatus(Permission.PermissionStatus.ACTIVE);
    }
} 