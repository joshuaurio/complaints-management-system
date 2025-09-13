// CreateUserRequest.java
package com.kcmc.complaints.dto;

public class CreateUserRequest {
    public String employeeId;
    public String username;
    public String email;
    public String password; // will be stored as hash
    public String firstName;
    public String lastName;
    public Short roleId;        // FK roles.id
    public Integer departmentId; // FK departments.id (nullable)
    public String role;
}
