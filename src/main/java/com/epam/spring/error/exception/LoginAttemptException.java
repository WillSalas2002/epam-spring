package com.epam.spring.error.exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class LoginAttemptException extends RuntimeException {
    public LoginAttemptException(String message) {
        super(message);
    }
}
