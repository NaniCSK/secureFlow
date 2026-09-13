package com.secureflow.authservice.dto;

import java.util.UUID;

public class AuthResponse {

    private UUID userId;
    private String email;
    private String message;

    public AuthResponse(UUID userId, String email, String message) {
        this.userId = userId;
        this.email = email;
        this.message = message;
    }

    public UUID getUserId() {
        return userId;
    }

    public String getEmail() {
        return email;
    }

    public String getMessage() {
        return message;
    }
}