package com.example.kiki.security;

import com.example.kiki.exception.RateLimitExceededException;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.ConsumptionProbe;
import org.springframework.stereotype.Service;
import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Service
public class RateLimiterService {
    private final ConcurrentMap<String, Bucket> buckets = new ConcurrentHashMap<>();

    private Bucket createBucket(int maxAttempts, Duration refillPeriod) {
        Bandwidth limit = Bandwidth.builder()
                .capacity(maxAttempts)
                .refillGreedy(maxAttempts, refillPeriod)
                .build();
        return Bucket.builder().addLimit(limit).build();
    }

    public void assertAllowed(String key, int maxAttempts, Duration refillPeriod) {
        Bucket bucket = buckets.computeIfAbsent(key, k -> createBucket(maxAttempts, refillPeriod));
        ConsumptionProbe probe = bucket.tryConsumeAndReturnRemaining(1);
        if (!probe.isConsumed()) {
            long secondsLeft = probe.getNanosToWaitForRefill() / 1_000_000_000;
            throw new RateLimitExceededException(
                    "Too many attempts. Try again in " + Math.max(1, secondsLeft / 60) + " minute(s).",
                    secondsLeft);
        }
    }
}