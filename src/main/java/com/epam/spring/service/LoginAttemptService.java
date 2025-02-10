package com.epam.spring.service;

import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class LoginAttemptService {

    private static final int INCREMENTOR = 1;
    private static final int MAX_ATTEMPTS = 3;
    private static final long LOCK_TIME_MILLIS = 5 * 60 * 1000;

    private final Map<String, FailedLoginAttempt> attemptsCache = new ConcurrentHashMap<>();

    public void loginFailed(String username) {
        FailedLoginAttempt attempt = attemptsCache.getOrDefault(username, new FailedLoginAttempt(0, null));

        int newAttemptCount = attempt.attemptCount() + INCREMENTOR;
        Instant lockTime = newAttemptCount >= MAX_ATTEMPTS ? Instant.now().plusMillis(LOCK_TIME_MILLIS) : attempt.lockTime();

        attemptsCache.put(username, new FailedLoginAttempt(newAttemptCount, lockTime));
    }

    public boolean isBlocked(String username) {
        FailedLoginAttempt attempt = attemptsCache.get(username);
        if (attempt == null || attempt.lockTime() == null) {
            return false;
        }
        if (Instant.now().isAfter(attempt.lockTime())) {
            attemptsCache.remove(username);
            return false;
        }
        return true;
    }

    public void resetAttempts(String username) {
        attemptsCache.remove(username);
    }

    private record FailedLoginAttempt(int attemptCount, Instant lockTime) {}
}
