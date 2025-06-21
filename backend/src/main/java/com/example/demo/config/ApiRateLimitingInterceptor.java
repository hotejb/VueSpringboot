package com.example.demo.config;

import io.github.bucket4j.Bucket;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class ApiRateLimitingInterceptor implements HandlerInterceptor {

    @Autowired
    private RateLimitingConfig rateLimitingConfig;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // Exclude auth endpoints from general API rate limiting
        if (request.getRequestURI().startsWith("/api/v2/auth")) {
            return true;
        }

        String clientIp = getClientIpAddress(request);
        Bucket bucket = rateLimitingConfig.getApiBucket(clientIp);

        if (!bucket.tryConsume(1)) {
            response.setStatus(429); // Too Many Requests
            response.getWriter().write("API请求过于频繁，请稍后再试");
            response.setContentType("application/json;charset=UTF-8");
            return false;
        }
        return true;
    }

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
}
