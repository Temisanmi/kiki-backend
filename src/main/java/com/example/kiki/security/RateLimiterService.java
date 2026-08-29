package com.example.kiki.security;

import com.example.kiki.exception.RateLimitExceededException;
import org.springframework.stereotype.Service;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Service
public class RateLimiterService {
    private static class LimitState {
        int attempts = 0;
        Instant windowStart = Instant.now();
        Instant blockedUntil = null;
    }

    private final ConcurrentMap<String, LimitState> states = new ConcurrentHashMap<>();

    private long minutesCeil(long seconds) {
        return Math.max(1, (seconds + 59) / 60);
    }

    public void assertNotBlocked(String key) {
        LimitState state = states.get(key);
        if (state == null) return;

        synchronized (state) {
            if (state.blockedUntil != null) {
                Instant now = Instant.now();
                if (now.isBefore(state.blockedUntil)) {
                    long secondsLeft = Duration.between(now, state.blockedUntil).getSeconds() + 1;
                    throw new RateLimitExceededException(
                            "Too many attempts. Try again in " + minutesCeil(secondsLeft) + " minute(s).",
                            secondsLeft);
                }
                state.blockedUntil = null;
                state.attempts = 0;
                state.windowStart = now;
            }
        }
    }

    public void recordAttempt(String key, int maxAttempts, Duration window, Duration lockoutDuration) {
        LimitState state = states.computeIfAbsent(key, k -> new LimitState());
        Instant now = Instant.now();
        synchronized (state) {
            if (now.isAfter(state.windowStart.plus(window))) {
                state.attempts = 0;
                state.windowStart = now;
            }
            state.attempts++;
            if (state.attempts >= maxAttempts) {
                state.blockedUntil = now.plus(lockoutDuration);
            }
        }
    }

    public void reset(String key) {
        states.remove(key);
    }
}