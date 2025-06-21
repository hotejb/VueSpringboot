package com.example.demo.config;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Component
public class RateLimitingConfig {

    private final Map<String, BucketWrapper> buckets = new ConcurrentHashMap<>();
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    public RateLimitingConfig() {
        // Schedule cleanup task to run every 5 minutes
        scheduler.scheduleAtFixedRate(this::cleanupExpiredBuckets, 5, 5, TimeUnit.MINUTES);
    }

    // Wrapper class to hold Bucket and its last access time
    private static class BucketWrapper {
        Bucket bucket;
        long lastAccessTime;

        public BucketWrapper(Bucket bucket) {
            this.bucket = bucket;
            this.lastAccessTime = System.currentTimeMillis();
        }

        public void updateLastAccessTime() {
            this.lastAccessTime = System.currentTimeMillis();
        }
    }

    // 登录限流：每分钟最多5次尝试
    public Bucket getLoginBucket(String clientIp) {
        BucketWrapper wrapper = buckets.computeIfAbsent(clientIp, k -> createLoginBucketWrapper());
        wrapper.updateLastAccessTime(); // Update access time on retrieval
        return wrapper.bucket;
    }

    private BucketWrapper createLoginBucketWrapper() {
        Bandwidth limit = Bandwidth.classic(5, Refill.intervally(5, Duration.ofMinutes(1)));
        return new BucketWrapper(Bucket.builder().addLimit(limit).build());
    }

    // API限流：每分钟最多100次请求
    public Bucket getApiBucket(String clientIp) {
        BucketWrapper wrapper = buckets.computeIfAbsent("api_" + clientIp, k -> createApiBucketWrapper());
        wrapper.updateLastAccessTime(); // Update access time on retrieval
        return wrapper.bucket;
    }

    private BucketWrapper createApiBucketWrapper() {
        Bandwidth limit = Bandwidth.classic(100, Refill.intervally(100, Duration.ofMinutes(1)));
        return new BucketWrapper(Bucket.builder().addLimit(limit).build());
    }

    // 清理过期的bucket
    public void cleanupExpiredBuckets() {
        long cutoffTime = System.currentTimeMillis() - Duration.ofHours(1).toMillis(); // Remove buckets not accessed in the last 1 hour
        buckets.entrySet().removeIf(entry -> entry.getValue().lastAccessTime < cutoffTime);
        System.out.println("Cleaned up expired buckets. Current size: " + buckets.size());
    }
}
