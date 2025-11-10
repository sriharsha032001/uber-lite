package com.uber_lite.uber_lite.ratelimit;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.Component;

@Component
public class RateLimiterService {

    private final Map<Long, TokenBucket> buckets = new ConcurrentHashMap<>();

    private final long refillIntervalMs;
    private final double capacity;

    public RateLimiterService() {
        this(1.0, TimeUnit.SECONDS.toMillis(5)); // capacity=1, refill=5s
    }

    public RateLimiterService(double capacity, long refillIntervalMs) {
        this.capacity = capacity;
        this.refillIntervalMs = refillIntervalMs;
    }

    /** Check and consume a token for a given riderId. Returns true if request allowed. */
    public boolean allowRequest(Long riderId) {
        if (riderId == null) return true; // if unknown rider, allow (or decide differently)
        TokenBucket bucket = buckets.computeIfAbsent(riderId, id -> new TokenBucket(capacity, refillIntervalMs));
        return bucket.tryConsume();
    }

        /** Optional: remove stale buckets to avoid memory leak (call periodically). */
    public void removeBucket(Long riderId) {
        buckets.remove(riderId);
    }

    
}
