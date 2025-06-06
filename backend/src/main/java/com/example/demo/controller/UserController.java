package com.example.demo.controller;

import com.example.demo.dto.ApiResponse;
import com.example.demo.entity.User;
import com.example.demo.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:3001"}, allowCredentials = "true")
public class UserController {
    
    @Autowired
    private UserService userService;
    
    // 获取用户列表（分页）
    @GetMapping
    public ResponseEntity<ApiResponse<Map<String, Object>>> getUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String role) {
        
        try {
            Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
            
            Pageable pageable = PageRequest.of(page, size, sort);
            Page<User> userPage;
            
            // 根据不同的查询条件调用不同的服务方法
            if ((search != null && !search.trim().isEmpty()) || 
                (status != null && !status.trim().isEmpty()) || 
                (role != null && !role.trim().isEmpty())) {
                userPage = userService.searchUsersWithFilters(search, status, role, pageable);
            } else {
                userPage = userService.getAllUsers(pageable);
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("users", userPage.getContent());
            response.put("currentPage", userPage.getNumber());
            response.put("totalItems", userPage.getTotalElements());
            response.put("totalPages", userPage.getTotalPages());
            response.put("hasNext", userPage.hasNext());
            response.put("hasPrevious", userPage.hasPrevious());
            
            return ResponseEntity.ok(ApiResponse.success("获取用户列表成功", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("获取用户列表失败: " + e.getMessage()));
        }
    }
    
    // 根据ID获取用户
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<User>> getUserById(@PathVariable Long id) {
        try {
            Optional<User> user = userService.findById(id);
            if (user.isPresent()) {
                return ResponseEntity.ok(ApiResponse.success("获取用户信息成功", user.get()));
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("获取用户信息失败: " + e.getMessage()));
        }
    }
    
    // 创建新用户
    @PostMapping
    public ResponseEntity<ApiResponse<User>> createUser(@Valid @RequestBody User user) {
        try {
            User savedUser = userService.createUser(user);
            return ResponseEntity.ok(ApiResponse.success("创建用户成功", savedUser));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("创建用户失败: " + e.getMessage()));
        }
    }
    
    // 更新用户
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<User>> updateUser(@PathVariable Long id, @Valid @RequestBody User user) {
        try {
            user.setId(id);
            User updatedUser = userService.updateUser(user);
            return ResponseEntity.ok(ApiResponse.success("更新用户成功", updatedUser));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("更新用户失败: " + e.getMessage()));
        }
    }
    
    // 删除用户
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable Long id) {
        try {
            userService.deleteUser(id);
            return ResponseEntity.ok(ApiResponse.success("删除用户成功"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("删除用户失败: " + e.getMessage()));
        }
    }
    
    // 批量删除用户
    @DeleteMapping("/batch")
    public ResponseEntity<ApiResponse<Void>> deleteUsers(@RequestBody Map<String, Object> request) {
        try {
            @SuppressWarnings("unchecked")
            java.util.List<Long> ids = (java.util.List<Long>) request.get("ids");
            userService.deleteUsers(ids);
            return ResponseEntity.ok(ApiResponse.success("批量删除用户成功"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("批量删除用户失败: " + e.getMessage()));
        }
    }
    
    // 更新用户状态
    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<User>> updateUserStatus(@PathVariable Long id, @RequestBody Map<String, String> request) {
        try {
            String status = request.get("status");
            User.UserStatus userStatus = User.UserStatus.valueOf(status);
            User updatedUser = userService.updateUserStatus(id, userStatus);
            return ResponseEntity.ok(ApiResponse.success("更新用户状态成功", updatedUser));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("更新用户状态失败: " + e.getMessage()));
        }
    }
    
    // 重置用户密码
    @PatchMapping("/{id}/password")
    public ResponseEntity<ApiResponse<Void>> resetPassword(@PathVariable Long id, @RequestBody Map<String, String> request) {
        try {
            String newPassword = request.get("password");
            userService.resetPassword(id, newPassword);
            return ResponseEntity.ok(ApiResponse.success("重置密码成功"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("重置密码失败: " + e.getMessage()));
        }
    }
} 