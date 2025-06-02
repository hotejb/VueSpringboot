package com.example.demo.controller;

import com.example.demo.dto.ApiResponse;
import com.example.demo.dto.LoginRequest;
import com.example.demo.entity.User;
import com.example.demo.service.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class AuthController {
    
    @Autowired
    private UserService userService;
    
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<Map<String, Object>>> login(
            @Valid @RequestBody LoginRequest loginRequest, 
            HttpSession session) {
        
        if (userService.authenticate(loginRequest.getUsername(), loginRequest.getPassword())) {
            Optional<User> userOpt = userService.findByUsername(loginRequest.getUsername());
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                session.setAttribute("userId", user.getId());
                session.setAttribute("username", user.getUsername());
                
                Map<String, Object> userData = new HashMap<>();
                userData.put("id", user.getId());
                userData.put("username", user.getUsername());
                userData.put("email", user.getEmail());
                userData.put("fullName", user.getFullName());
                
                return ResponseEntity.ok(ApiResponse.success("登录成功", userData));
            }
        }
        
        return ResponseEntity.badRequest().body(ApiResponse.error("用户名或密码错误"));
    }
    
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(HttpSession session) {
        session.invalidate();
        return ResponseEntity.ok(ApiResponse.success("退出登录成功"));
    }
    
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getCurrentUser(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        String username = (String) session.getAttribute("username");
        
        if (userId != null && username != null) {
            Optional<User> userOpt = userService.findByUsername(username);
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                Map<String, Object> userData = new HashMap<>();
                userData.put("id", user.getId());
                userData.put("username", user.getUsername());
                userData.put("email", user.getEmail());
                userData.put("fullName", user.getFullName());
                
                return ResponseEntity.ok(ApiResponse.success("获取用户信息成功", userData));
            }
        }
        
        return ResponseEntity.status(401).body(ApiResponse.error("未登录"));
    }
} 