package com.example.demo.repository;

import com.example.demo.entity.Permission;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long> {
    
    Optional<Permission> findByName(String name);
    
    Optional<Permission> findByCode(String code);
    
    boolean existsByName(String name);
    
    boolean existsByCode(String code);
    
    // 按状态查询权限
    Page<Permission> findByStatus(Permission.PermissionStatus status, Pageable pageable);
    
    // 按模块查询权限
    Page<Permission> findByModule(String module, Pageable pageable);
    
    // 按类型查询权限
    Page<Permission> findByType(Permission.PermissionType type, Pageable pageable);
    
    // 搜索权限（按名称、代码或模块）
    Page<Permission> findByNameContainingOrCodeContainingOrModuleContaining(
        String name, String code, String module, Pageable pageable);
    
    // 复合查询：搜索文本 + 状态
    Page<Permission> findByStatusAndNameContainingOrStatusAndCodeContainingOrStatusAndModuleContaining(
        Permission.PermissionStatus status1, String name, 
        Permission.PermissionStatus status2, String code,
        Permission.PermissionStatus status3, String module,
        Pageable pageable);
    
    // 复合查询：搜索文本 + 模块
    Page<Permission> findByModuleAndNameContainingOrModuleAndCodeContaining(
        String module1, String name, 
        String module2, String code, 
        Pageable pageable);
    
    // 获取所有启用的权限，按模块和排序字段排序
    List<Permission> findByStatusOrderByModuleAscSortOrderAsc(Permission.PermissionStatus status);
    
    // 按模块获取启用的权限
    List<Permission> findByModuleAndStatusOrderBySortOrderAsc(String module, Permission.PermissionStatus status);
    
    // 获取所有模块名称
    List<String> findDistinctModuleByStatus(Permission.PermissionStatus status);
} 