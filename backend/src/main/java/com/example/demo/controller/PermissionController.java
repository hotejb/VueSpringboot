package com.example.demo.controller;

import com.example.demo.dto.ApiResponse;
import com.example.demo.entity.Permission;
import com.example.demo.service.PermissionService;
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

@RestController
@RequestMapping("/api/permissions")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:3001"}, allowCredentials = "true")
public class PermissionController {
    
    @Autowired
    private PermissionService permissionService;
    
    // 获取权限列表（分页）
    @GetMapping
    public ResponseEntity<ApiResponse<Map<String, Object>>> getPermissions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String module,
            @RequestParam(required = false) String type) {
        
        try {
            Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
            
            Pageable pageable = PageRequest.of(page, size, sort);
            Page<Permission> permissionPage;
            
            // 根据不同的查询条件调用不同的服务方法
            if ((search != null && !search.trim().isEmpty()) || 
                (status != null && !status.trim().isEmpty()) ||
                (module != null && !module.trim().isEmpty()) ||
                (type != null && !type.trim().isEmpty())) {
                permissionPage = permissionService.searchPermissionsWithFilters(search, status, module, type, pageable);
            } else {
                permissionPage = permissionService.getAllPermissions(pageable);
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("permissions", permissionPage.getContent());
            response.put("currentPage", permissionPage.getNumber());
            response.put("totalItems", permissionPage.getTotalElements());
            response.put("totalPages", permissionPage.getTotalPages());
            response.put("hasNext", permissionPage.hasNext());
            response.put("hasPrevious", permissionPage.hasPrevious());
            
            return ResponseEntity.ok(ApiResponse.success("获取权限列表成功", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("获取权限列表失败: " + e.getMessage()));
        }
    }
    
    // 根据ID获取权限
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Permission>> getPermissionById(@PathVariable Long id) {
        try {
            Optional<Permission> permissionOpt = permissionService.findById(id);
            if (permissionOpt.isPresent()) {
                return ResponseEntity.ok(ApiResponse.success("获取权限成功", permissionOpt.get()));
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("获取权限失败: " + e.getMessage()));
        }
    }
    
    // 创建权限
    @PostMapping
    public ResponseEntity<ApiResponse<Permission>> createPermission(@Valid @RequestBody Permission permission) {
        try {
            Permission createdPermission = permissionService.createPermission(permission);
            return ResponseEntity.ok(ApiResponse.success("权限创建成功", createdPermission));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("权限创建失败: " + e.getMessage()));
        }
    }
    
    // 更新权限
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Permission>> updatePermission(@PathVariable Long id, @Valid @RequestBody Permission permission) {
        try {
            permission.setId(id);
            Permission updatedPermission = permissionService.updatePermission(permission);
            return ResponseEntity.ok(ApiResponse.success("权限更新成功", updatedPermission));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("权限更新失败: " + e.getMessage()));
        }
    }
    
    // 删除权限
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deletePermission(@PathVariable Long id) {
        try {
            permissionService.deletePermission(id);
            return ResponseEntity.ok(ApiResponse.success("权限删除成功", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("权限删除失败: " + e.getMessage()));
        }
    }
    
    // 批量删除权限
    @DeleteMapping("/batch")
    public ResponseEntity<ApiResponse<Void>> deletePermissions(@RequestBody List<Long> ids) {
        try {
            permissionService.deletePermissions(ids);
            return ResponseEntity.ok(ApiResponse.success("权限批量删除成功", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("权限批量删除失败: " + e.getMessage()));
        }
    }
    
    // 更新权限状态
    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<Permission>> updatePermissionStatus(
            @PathVariable Long id, 
            @RequestBody Map<String, String> statusMap) {
        try {
            String statusStr = statusMap.get("status");
            Permission.PermissionStatus status = Permission.PermissionStatus.valueOf(statusStr.toUpperCase());
            Permission updatedPermission = permissionService.updatePermissionStatus(id, status);
            return ResponseEntity.ok(ApiResponse.success("权限状态更新成功", updatedPermission));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("权限状态更新失败: " + e.getMessage()));
        }
    }
    
    // 获取所有启用的权限
    @GetMapping("/active")
    public ResponseEntity<ApiResponse<List<Permission>>> getActivePermissions() {
        try {
            List<Permission> activePermissions = permissionService.getActivePermissions();
            return ResponseEntity.ok(ApiResponse.success("获取启用权限成功", activePermissions));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("获取启用权限失败: " + e.getMessage()));
        }
    }
    
    // 按模块获取启用的权限
    @GetMapping("/active/module/{module}")
    public ResponseEntity<ApiResponse<List<Permission>>> getActivePermissionsByModule(@PathVariable String module) {
        try {
            List<Permission> permissions = permissionService.getActivePermissionsByModule(module);
            return ResponseEntity.ok(ApiResponse.success("获取模块权限成功", permissions));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("获取模块权限失败: " + e.getMessage()));
        }
    }
    
    // 获取所有模块名称
    @GetMapping("/modules")
    public ResponseEntity<ApiResponse<List<String>>> getAllModules() {
        try {
            List<String> modules = permissionService.getAllModules();
            return ResponseEntity.ok(ApiResponse.success("获取模块列表成功", modules));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("获取模块列表失败: " + e.getMessage()));
        }
    }
} 