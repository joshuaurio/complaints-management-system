package com.kcmc.complaints.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public class UpdateUserRequest {
    @Size(max = 20, message = "Employee ID must not exceed 20 characters")
    public String employeeId;

    @Size(min = 3, max = 100, message = "Username must be between 3 and 100 characters")
    public String username;

    @Email(message = "Valid email is required")
    @Size(max = 255, message = "Email must not exceed 255 characters")
    public String email;

    @Size(min = 8, message = "Password must be at least 8 characters")
    public String password;

    @Size(max = 100, message = "First name must not exceed 100 characters")
    public String firstName;

    @Size(max = 100, message = "Last name must not exceed 100 characters")
    public String lastName;

    public Short roleId; // FK roles.id - admin only
    public Integer departmentId; // FK departments.id - admin only
    public Boolean isActive; // admin only
}