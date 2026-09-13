package com.secureflow.authservice.dto;

public class LoginResponse {

    private String accessToken;
    private String tokenType;
    private String message;
    private String refreshToken;

    public LoginResponse(
            String accessToken,
            String refreshToken,
            String tokenType,
            String message) {

        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.tokenType = tokenType;
        this.message = message;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public String getTokenType() {
        return tokenType;
    }

    public String getMessage() {
        return message;
    }
}