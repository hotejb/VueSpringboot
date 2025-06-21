package com.example.demo.controller;

import com.example.demo.config.RateLimitingConfig;
import com.example.demo.dto.ApiResponse;
import com.example.demo.dto.LoginRequest;
import com.example.demo.entity.User;
import com.example.demo.service.UserService;
import com.example.demo.util.JwtUtil;
import io.github.bucket4j.Bucket;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/v2/auth")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:3001"}, 
             allowCredentials = "true",
             allowedHeaders = {"Authorization", "Content-Type"},
             methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS})
public class JwtAuthController {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Autowired
    private RateLimitingConfig rateLimitingConfig;
    
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<Map<String, Object>>> login(
            @Valid @RequestBody LoginRequest loginRequest,
            HttpServletRequest request) {
        
        String clientIp = getClientIpAddress(request);
        Bucket bucket = rateLimitingConfig.getLoginBucket(clientIp);
        
        // 检查限流
        if (!bucket.tryConsume(1)) {
            return ResponseEntity.status(429)
                    .body(ApiResponse.error("登录尝试过于频繁，请稍后再试"));
        }
        
        try {
            // 验证用户凭据
            if (userService.authenticate(loginRequest.getUsername(), loginRequest.getPassword())) {
                Optional<User> userOpt = userService.findByUsername(loginRequest.getUsername());
                if (userOpt.isPresent()) {
                    User user = userOpt.get();
                    
                    // 检查用户状态
                    if (user.getStatus() != User.UserStatus.ACTIVE) {
                        return ResponseEntity.badRequest()
                                .body(ApiResponse.error("账户已被禁用或待审核"));
                    }
                    
                    // 更新最后登录时间
                    userService.updateLastLogin(user.getUsername());
                    
                    // 生成JWT令牌
                    UserDetails userDetails = userService.loadUserByUsername(user.getUsername());
                    String accessToken = jwtUtil.generateToken(userDetails);
                    String refreshToken = jwtUtil.generateRefreshToken(userDetails);
                    
                    Map<String, Object> response = new HashMap<>();
                    response.put("accessToken", accessToken);
                    response.put("refreshToken", refreshToken);
                    response.put("tokenType", "Bearer");
                    response.put("expiresIn", jwtUtil.getAccessTokenExpirationInSeconds());
                    response.put("user", Map.of(
                            "id", user.getId(),
                            "username", user.getUsername(),
                            "email", user.getEmail(),
                            "fullName", user.getFullName(),
                            "role", user.getRole(),
                            "status", user.getStatus()
                    ));
                    
                    return ResponseEntity.ok(ApiResponse.success("登录成功", response));
                }
            }
            
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("用户名或密码错误"));
                    
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(ApiResponse.error("登录过程中发生错误"));
        }
    }
    
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<Map<String, Object>>> refreshToken(
            @RequestBody Map<String, String> request) {
        
        String refreshToken = request.get("refreshToken");
        if (refreshToken == null || refreshToken.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("刷新令牌不能为空"));
        }
        
        try {
            // 验证刷新令牌
            if (jwtUtil.validateTokenFormat(refreshToken) && jwtUtil.isRefreshToken(refreshToken)) {
                String username = jwtUtil.getUsernameFromToken(refreshToken);
                UserDetails userDetails = userService.loadUserByUsername(username);
                
                if (jwtUtil.validateToken(refreshToken, userDetails)) {
                    String newAccessToken = jwtUtil.generateToken(userDetails);
                    
                    Map<String, Object> response = new HashMap<>();
                    response.put("accessToken", newAccessToken);
                    response.put("tokenType", "Bearer");
                    response.put("expiresIn", jwtUtil.getAccessTokenExpirationInSeconds());
                    
                    return ResponseEntity.ok(ApiResponse.success("令牌刷新成功", response));
                }
            }
            
            return ResponseEntity.status(401)
                    .body(ApiResponse.error("无效的刷新令牌"));
                    
        } catch (Exception e) {
            return ResponseEntity.status(401)
                    .body(ApiResponse.error("令牌刷新失败"));
        }
    }
    
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout() {
        // JWT是无状态的，客户端删除令牌即可
        // 在生产环境中，可以将令牌加入黑名单
        return ResponseEntity.ok(ApiResponse.success("退出登录成功"));
    }
    
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getCurrentUser(
            HttpServletRequest request) {
        
        try {
            String token = extractTokenFromRequest(request);
            if (token != null && jwtUtil.validateTokenFormat(token)) {
                String username = jwtUtil.getUsernameFromToken(token);
                Optional<User> userOpt = userService.findByUsername(username);
                
                if (userOpt.isPresent()) {
                    User user = userOpt.get();
                    Map<String, Object> userData = new HashMap<>();
                    userData.put("id", user.getId());
                    userData.put("username", user.getUsername());
                    userData.put("email", user.getEmail());
                    userData.put("fullName", user.getFullName());
                    userData.put("role", user.getRole());
                    userData.put("status", user.getStatus());
                    
                    return ResponseEntity.ok(ApiResponse.success("获取用户信息成功", userData));
                }
            }
            
            return ResponseEntity.status(401)
                    .body(ApiResponse.error("未登录或令牌无效"));
                    
        } catch (Exception e) {
            return ResponseEntity.status(401)
                    .body(ApiResponse.error("获取用户信息失败"));
        }
    }
    
    // 获取客户端IP地址
    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp;
        }
        
        return request.getRemoteAddr();
    }
    
    // 从请求中提取JWT令牌
    private String extractTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
