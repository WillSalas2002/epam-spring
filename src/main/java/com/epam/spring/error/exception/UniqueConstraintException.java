package com.epam.spring.error.exception;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UniqueConstraintException extends RuntimeException {
    private String message;

    public UniqueConstraintException(String message) {
        this.message = message;
    }
}
