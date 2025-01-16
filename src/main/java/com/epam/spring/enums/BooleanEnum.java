package com.epam.spring.enums;

import lombok.Getter;

@Getter
public enum BooleanEnum {
    TRUE("true"),
    FALSE("false");

    private final String value;

    BooleanEnum(String value) {
        this.value = value;
    }

    public static BooleanEnum fromString(String value) {
        return value.equals("true") ? TRUE : FALSE;
    }
}
