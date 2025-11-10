package com.uber_lite.uber_lite.ratelimit;

public class TokenBucket {

    private final long refillIntervalMs; // e.g. 5000 ms for 1 token per 5s
    private final double capacity;       // max tokens
    private double tokens;
    private long lastRefillTs;

    public TokenBucket(double capacity, long refillIntervalMs) {
        this.capacity = capacity;
        this.refillIntervalMs = refillIntervalMs;
        this.tokens = capacity;
        this.lastRefillTs = System.currentTimeMillis();
    }

    public synchronized boolean tryConsume() {
        refill();
        if (tokens >= 1.0) {
            tokens -= 1.0;
            return true;
        }
        return false;
    }

    private void refill() {
        long now = System.currentTimeMillis();
        if (now <= lastRefillTs) return;
        long elapsed = now - lastRefillTs;
        // how many intervals passed
        double intervals = (double) elapsed / (double) refillIntervalMs;
        if (intervals <= 0.0) return;
        double toAdd = intervals; // 1 token per interval
        tokens = Math.min(capacity, tokens + toAdd);
        lastRefillTs = now;
    }
}
    
