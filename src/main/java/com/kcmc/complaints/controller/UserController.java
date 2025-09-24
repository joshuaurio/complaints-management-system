package com.kcmc.complaints.controller;

import com.kcmc.complaints.dto.CreateUserRequest;
import com.kcmc.complaints.dto.UpdateUserRequest;
import com.kcmc.complaints.model.User;
import com.kcmc.complaints.service.UserService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@CrossOrigin
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    private final UserService svc;

    public UserController(UserService svc) {
        this.svc = svc;
    }

    // Structured error response
    private ResponseEntity<Map<String, Object>> errorResponse(String errorCode, String message, int status) {
        Map<String, Object> error = new HashMap<>();
        error.put("errorCode", errorCode);
        error.put("message", message);
        return ResponseEntity.status(status).body(error);
    }

    // ✅ Get all users (Admin only)
    @GetMapping
    @PreAuthorize("hasRole('ROLE_ADMINISTRATOR')")
    public Page<User> all(Pageable pageable) {
        logger.info("Fetching all users with pagination");
        return svc.findAll(pageable);
    }

    // ✅ Get single user (Admin or self)
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMINISTRATOR') or authentication.principal.username == #svc.findById(#id).username")
    public User one(@PathVariable Long id) {
        logger.info("Fetching user with ID: {}", id);
        return svc.findById(id);
    }

    // ✅ Self-registration endpoint for STAFF users
    @PostMapping("/register")
    public ResponseEntity<?> registerStaff(@RequestBody @Valid CreateUserRequest req) {
        try {
            logger.info("Staff registration attempt for username: {}", req.username);

            // Validate role for self-registration
            if (req.roleId != null) {
                // Check if the provided roleId corresponds to STAFF
                if (!"STAFF".equalsIgnoreCase(getRoleNameById(req.roleId))) {
                    return errorResponse("INVALID_ROLE",
                            "Self-registration only allows STAFF role. Use /api/users for admin registrations.", 400);
                }
            } else {
                // Default to STAFF if not provided
                req.roleId = getStaffRoleId();
            }

            // Call service to create user
            User createdUser = svc.createStaffUser(req);
            logger.info("Staff user registered successfully: {}", req.username);

            return ResponseEntity.ok(Map.of(
                    "message", "Registration successful",
                    "userId", createdUser.getId(),
                    "username", createdUser.getUsername()
            ));
        } catch (Exception e) {
            logger.error("Error during staff registration for username: {}", req.username, e);
            return errorResponse("REGISTRATION_FAILED", "Registration failed: " + e.getMessage(), 400);
        }
    }

    // ✅ Admin-only user creation for ADMINISTRATOR and ICT_AGENT
    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMINISTRATOR')")
    public ResponseEntity<?> createAdminUser(@RequestBody @Valid CreateUserRequest req, Authentication authentication) {
        try {
            logger.info("Admin user creation attempt by {} for username: {}",
                    authentication.getName(), req.username);

            // Validate role for admin creation
            String roleName = getRoleNameById(req.roleId);
            if (roleName == null || !("ADMINISTRATOR".equalsIgnoreCase(roleName) || "ICT_AGENT".equalsIgnoreCase(roleName))) {
                return errorResponse("INVALID_ROLE",
                        "Administrators can only create ADMINISTRATOR or ICT_AGENT users. For STAFF, use /api/users/register.", 400);
            }

            // Call service to create user
            User createdUser = svc.create(req);
            logger.info("Admin created user successfully: {} with role {}", req.username, roleName);

            return ResponseEntity.ok(Map.of(
                    "message", "User created successfully",
                    "userId", createdUser.getId(),
                    "username", createdUser.getUsername(),
                    "role", roleName
            ));
        } catch (Exception e) {
            logger.error("Error during admin user creation for username: {}", req.username, e);
            return errorResponse("CREATION_FAILED", "User creation failed: " + e.getMessage(), 400);
        }
    }

    // ✅ Update user details (Admin or self)
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMINISTRATOR') or authentication.principal.username == #svc.findById(#id).username")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody @Valid UpdateUserRequest req, Authentication authentication) {
        try {
            boolean isAdmin = authentication.getAuthorities().stream()
                    .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMINISTRATOR"));
            logger.info("Update attempt for user ID: {} by {} (isAdmin: {})", id, authentication.getName(), isAdmin);

            User updatedUser = svc.update(id, req, isAdmin);
            logger.info("User ID: {} updated successfully by {}", id, authentication.getName());

            return ResponseEntity.ok(Map.of(
                    "message", "User updated successfully",
                    "userId", updatedUser.getId(),
                    "username", updatedUser.getUsername()
            ));
        } catch (Exception e) {
            logger.error("Error updating user ID: {} by {}", id, authentication.getName(), e);
            return errorResponse("UPDATE_FAILED", "User update failed: " + e.getMessage(), 400);
        }
    }

    // ✅ Delete user (Admin only)
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMINISTRATOR')")
    public ResponseEntity<?> delete(@PathVariable Long id, Authentication authentication) {
        try {
            logger.info("Admin {} attempting to delete user ID: {}", authentication.getName(), id);
            svc.delete(id);
            return ResponseEntity.ok(Map.of("message", "User deleted successfully"));
        } catch (Exception e) {
            logger.error("Error deleting user ID: {} by admin {}", id, authentication.getName(), e);
            return errorResponse("DELETION_FAILED", "User deletion failed: " + e.getMessage(), 400);
        }
    }

    // Helper methods (replace with RoleRepository in production)
    private String getRoleNameById(Short roleId) {
        if (roleId == null) return null;
        switch (roleId) {
            case 1: return "STAFF";
            case 2: return "ADMINISTRATOR";
            case 3: return "ICT_AGENT";
            default: return null;
        }
    }

    private Short getStaffRoleId() {
        return 1; // STAFF role ID
    }
}