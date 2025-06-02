package com.example.demo.config;

import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {
    
    @Autowired
    private UserService userService;
    
    @Override
    public void run(String... args) throws Exception {
        // 创建默认用户
        if (!userService.existsByUsername("admin")) {
            userService.createUser("admin", "123456", "admin@example.com", "管理员");
            System.out.println("默认用户已创建: admin/123456");
        }
        
        if (!userService.existsByUsername("user")) {
            userService.createUser("user", "123456", "user@example.com", "普通用户");
            System.out.println("默认用户已创建: user/123456");
        }
    }
} 