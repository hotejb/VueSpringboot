package com.example.demo.controller;

import com.example.demo.dto.ApiResponse;
import com.example.demo.entity.Role;
import com.example.demo.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@RestController
@RequestMapping("/api/roles")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:3001"}, allowCredentials = "true")
public class RoleController {
    
    @Autowired
    private RoleService roleService;
    
    // 获取角色列表（分页）
    @GetMapping
    public ResponseEntity<ApiResponse<Map<String, Object>>> getRoles(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String status) {
        
        try {
            Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
            
            Pageable pageable = PageRequest.of(page, size, sort);
            Page<Role> rolePage;
            
            // 根据不同的查询条件调用不同的服务方法
            if ((search != null && !search.trim().isEmpty()) || 
                (status != null && !status.trim().isEmpty())) {
                rolePage = roleService.searchRolesWithFilters(search, status, pageable);
            } else {
                rolePage = roleService.getAllRoles(pageable);
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("roles", rolePage.getContent());
            response.put("currentPage", rolePage.getNumber());
            response.put("totalItems", rolePage.getTotalElements());
            response.put("totalPages", rolePage.getTotalPages());
            response.put("hasNext", rolePage.hasNext());
            response.put("hasPrevious", rolePage.hasPrevious());
            
            return ResponseEntity.ok(ApiResponse.success("获取角色列表成功", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("获取角色列表失败: " + e.getMessage()));
        }
    }
    
    // 根据ID获取角色
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Role>> getRoleById(@PathVariable Long id) {
        try {
            Optional<Role> roleOpt = roleService.findById(id);
            if (roleOpt.isPresent()) {
                return ResponseEntity.ok(ApiResponse.success("获取角色成功", roleOpt.get()));
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("获取角色失败: " + e.getMessage()));
        }
    }
    
    // 根据ID获取角色（包含权限）
    @GetMapping("/{id}/permissions")
    public ResponseEntity<ApiResponse<Role>> getRoleWithPermissions(@PathVariable Long id) {
        try {
            Optional<Role> roleOpt = roleService.findByIdWithPermissions(id);
            if (roleOpt.isPresent()) {
                return ResponseEntity.ok(ApiResponse.success("获取角色权限成功", roleOpt.get()));
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("获取角色权限失败: " + e.getMessage()));
        }
    }
    
    // 创建角色
    @PostMapping
    public ResponseEntity<ApiResponse<Role>> createRole(@Valid @RequestBody Role role) {
        try {
            Role createdRole = roleService.createRole(role);
            return ResponseEntity.ok(ApiResponse.success("角色创建成功", createdRole));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("角色创建失败: " + e.getMessage()));
        }
    }
    
    // 更新角色
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Role>> updateRole(@PathVariable Long id, @Valid @RequestBody Role role) {
        try {
            role.setId(id);
            Role updatedRole = roleService.updateRole(role);
            return ResponseEntity.ok(ApiResponse.success("角色更新成功", updatedRole));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("角色更新失败: " + e.getMessage()));
        }
    }
    
    // 删除角色
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteRole(@PathVariable Long id) {
        try {
            roleService.deleteRole(id);
            return ResponseEntity.ok(ApiResponse.success("角色删除成功", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("角色删除失败: " + e.getMessage()));
        }
    }
    
    // 批量删除角色
    @DeleteMapping("/batch")
    public ResponseEntity<ApiResponse<Void>> deleteRoles(@RequestBody List<Long> ids) {
        try {
            roleService.deleteRoles(ids);
            return ResponseEntity.ok(ApiResponse.success("角色批量删除成功", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("角色批量删除失败: " + e.getMessage()));
        }
    }
    
    // 更新角色状态
    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<Role>> updateRoleStatus(
            @PathVariable Long id, 
            @RequestBody Map<String, String> statusMap) {
        try {
            String statusStr = statusMap.get("status");
            Role.RoleStatus status = Role.RoleStatus.valueOf(statusStr.toUpperCase());
            Role updatedRole = roleService.updateRoleStatus(id, status);
            return ResponseEntity.ok(ApiResponse.success("角色状态更新成功", updatedRole));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("角色状态更新失败: " + e.getMessage()));
        }
    }
    
    // 获取所有启用的角色
    @GetMapping("/active")
    public ResponseEntity<ApiResponse<List<Role>>> getActiveRoles() {
        try {
            List<Role> activeRoles = roleService.getActiveRoles();
            return ResponseEntity.ok(ApiResponse.success("获取启用角色成功", activeRoles));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("获取启用角色失败: " + e.getMessage()));
        }
    }
    
    // 为角色分配权限
    @PutMapping("/{id}/permissions")
    public ResponseEntity<ApiResponse<Role>> assignPermissions(
            @PathVariable Long id, 
            @RequestBody Map<String, Set<Long>> permissionMap) {
        try {
            Set<Long> permissionIds = permissionMap.get("permissionIds");
            Role updatedRole = roleService.assignPermissions(id, permissionIds);
            return ResponseEntity.ok(ApiResponse.success("角色权限分配成功", updatedRole));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("角色权限分配失败: " + e.getMessage()));
        }
    }
    
    // 为角色添加权限
    @PostMapping("/{id}/permissions")
    public ResponseEntity<ApiResponse<Role>> addPermissions(
            @PathVariable Long id, 
            @RequestBody Map<String, Set<Long>> permissionMap) {
        try {
            Set<Long> permissionIds = permissionMap.get("permissionIds");
            Role updatedRole = roleService.addPermissions(id, permissionIds);
            return ResponseEntity.ok(ApiResponse.success("角色权限添加成功", updatedRole));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("角色权限添加失败: " + e.getMessage()));
        }
    }
    
    // 为角色移除权限
    @DeleteMapping("/{id}/permissions")
    public ResponseEntity<ApiResponse<Role>> removePermissions(
            @PathVariable Long id, 
            @RequestBody Map<String, Set<Long>> permissionMap) {
        try {
            Set<Long> permissionIds = permissionMap.get("permissionIds");
            Role updatedRole = roleService.removePermissions(id, permissionIds);
            return ResponseEntity.ok(ApiResponse.success("角色权限移除成功", updatedRole));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("角色权限移除失败: " + e.getMessage()));
        }
    }
    
    // 获取所有角色及其权限
    @GetMapping("/with-permissions")
    public ResponseEntity<ApiResponse<List<Role>>> getAllRolesWithPermissions() {
        try {
            List<Role> rolesWithPermissions = roleService.getAllRolesWithPermissions();
            return ResponseEntity.ok(ApiResponse.success("获取角色权限列表成功", rolesWithPermissions));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("获取角色权限列表失败: " + e.getMessage()));
        }
    }
} 