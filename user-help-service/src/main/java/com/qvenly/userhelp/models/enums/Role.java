package com.qvenly.userhelp.models.enums;

public enum Role {
    ADMIN,
    ORGANIZER,
    USER;

    public static Role fromNullable(String value) {
        if (value == null || value.isBlank()) {
            return USER;
        }
        try {
            return Role.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            return USER;
        }
    }
}
