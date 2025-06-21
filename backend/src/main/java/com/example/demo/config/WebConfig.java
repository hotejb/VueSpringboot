package com.example.demo.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private ApiRateLimitingInterceptor apiRateLimitingInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(apiRateLimitingInterceptor)
                .addPathPatterns("/api/**") // Apply to all /api endpoints
                .excludePathPatterns("/api/v2/auth/**"); // Exclude auth endpoints
    }
}
