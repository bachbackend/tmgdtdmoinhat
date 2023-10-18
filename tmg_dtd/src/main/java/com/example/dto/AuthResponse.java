package com.example.dto;

public class AuthResponse {
    private String userName;
    private String accessToken;

    public AuthResponse() {
    }

    public AuthResponse(String userName, String accessToken) {
        this.userName = userName;
        this.accessToken = accessToken;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
}
