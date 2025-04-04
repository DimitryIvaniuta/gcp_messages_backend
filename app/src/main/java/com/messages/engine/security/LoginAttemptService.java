package com.messages.engine.security;

import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Service to track and limit login attempts, preventing brute force attacks.
 */
@Service
public class LoginAttemptService {

    private static final int MAX_ATTEMPTS = 5;
    private static final long LOCK_TIME_MILLIS = 15 * 60 * 1000;

    // Map of username -> (numberOfAttempts, lockExpirationTime)
    private final Map<String, AttemptStatus> attempts = new ConcurrentHashMap<>();

    /**
     * Record a failed login attempt for the given username.
     */
    public void loginFailed(String username) {
        AttemptStatus status = attempts.getOrDefault(username, new AttemptStatus(0, 0));
        int newAttempts = status.attemptCount + 1;
        long lockTime = status.lockUntil;

        if (newAttempts >= MAX_ATTEMPTS) {
            lockTime = Instant.now().toEpochMilli() + LOCK_TIME_MILLIS;
        }
        attempts.put(username, new AttemptStatus(newAttempts, lockTime));
    }

    /**
     * Record a successful login attempt, resetting the attempt count.
     */
    public void loginSucceeded(String username) {
        attempts.remove(username);
    }

    /**
     * Checks if the user is locked out.
     */
    public boolean isBlocked(String username) {
        AttemptStatus status = attempts.get(username);
        if (status == null) {
            return false;
        }
        // If lockUntil is in the future, user is blocked
        return Instant.now().toEpochMilli() < status.lockUntil;
    }

    private record AttemptStatus(int attemptCount, long lockUntil) { }
}
