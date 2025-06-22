package com.example.demo.repository;

import com.example.demo.entity.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    
    Optional<Role> findByName(String name);
    
    Optional<Role> findByCode(String code);
    
    boolean existsByName(String name);
    
    boolean existsByCode(String code);
    
    // 按状态查询角色
    Page<Role> findByStatus(Role.RoleStatus status, Pageable pageable);
    
    // 搜索角色（按名称或代码）
    Page<Role> findByNameContainingOrCodeContaining(String name, String code, Pageable pageable);
    
    // 复合查询：搜索文本 + 状态
    Page<Role> findByStatusAndNameContainingOrStatusAndCodeContaining(
        Role.RoleStatus status1, String name, 
        Role.RoleStatus status2, String code, 
        Pageable pageable);
    
    // 获取所有启用的角色，按排序字段排序
    List<Role> findByStatusOrderBySortOrderAsc(Role.RoleStatus status);
    
    // 查询角色及其权限
    @Query("SELECT r FROM Role r LEFT JOIN FETCH r.permissions WHERE r.id = :id")
    Optional<Role> findByIdWithPermissions(@Param("id") Long id);
    
    // 查询所有角色及其权限
    @Query("SELECT r FROM Role r LEFT JOIN FETCH r.permissions")
    List<Role> findAllWithPermissions();
} 