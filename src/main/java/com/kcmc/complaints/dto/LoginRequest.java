package com.kcmc.complaints.dto;

public class LoginRequest {
    public String username;
    public String password;

    // Default constructor
    public LoginRequest() {}

    public LoginRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }
}