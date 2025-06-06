package com.example.demo.config;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RateLimitingConfig {
    
    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();
    
    // 登录限流：每分钟最多5次尝试
    public Bucket getLoginBucket(String clientIp) {
        return buckets.computeIfAbsent(clientIp, this::createLoginBucket);
    }
    
    private Bucket createLoginBucket(String clientIp) {
        Bandwidth limit = Bandwidth.classic(5, Refill.intervally(5, Duration.ofMinutes(1)));
        return Bucket.builder()
                .addLimit(limit)
                .build();
    }
    
    // API限流：每分钟最多100次请求
    public Bucket getApiBucket(String clientIp) {
        return buckets.computeIfAbsent("api_" + clientIp, this::createApiBucket);
    }
    
    private Bucket createApiBucket(String clientIp) {
        Bandwidth limit = Bandwidth.classic(100, Refill.intervally(100, Duration.ofMinutes(1)));
        return Bucket.builder()
                .addLimit(limit)
                .build();
    }
    
    // 清理过期的bucket
    public void cleanupExpiredBuckets() {
        // 这里可以实现清理逻辑，定期清理不活跃的IP
        // 为了简化，暂时不实现
    }
} 