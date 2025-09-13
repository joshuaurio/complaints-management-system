package com.kcmc.complaints.dto;

public class LoginResponse {
    public String token;
    public Long userId;
    public String username;
    public String role;
    public String firstName;
    public String lastName;

    public LoginResponse() {}

    public LoginResponse(String token, Long userId, String username, String role, String firstName, String lastName) {
        this.token = token;
        this.userId = userId;
        this.username = username;
        this.role = role;
        this.firstName = firstName;
        this.lastName = lastName;
    }
}