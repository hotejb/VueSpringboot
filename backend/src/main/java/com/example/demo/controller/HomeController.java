package com.example.demo.controller;

import com.example.demo.dto.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class HomeController {
    
    @GetMapping("/home")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getHomeData() {
        Map<String, Object> homeData = new HashMap<>();
        homeData.put("title", "欢迎来到我们的系统");
        homeData.put("description", "这是一个现代化的前后端分离系统");
        homeData.put("currentTime", LocalDateTime.now());
        homeData.put("features", new String[]{
            "用户认证系统",
            "现代化UI设计", 
            "响应式布局",
            "安全的API接口"
        });
        
        return ResponseEntity.ok(ApiResponse.success("获取首页数据成功", homeData));
    }
    
    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalUsers", 1250);
        stats.put("activeUsers", 892);
        stats.put("totalOrders", 3456);
        stats.put("revenue", 125000.50);
        
        return ResponseEntity.ok(ApiResponse.success("获取统计数据成功", stats));
    }
} 