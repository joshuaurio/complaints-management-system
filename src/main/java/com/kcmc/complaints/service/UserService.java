package com.kcmc.complaints.service;

import com.kcmc.complaints.dto.CreateUserRequest;
import com.kcmc.complaints.dto.UpdateUserRequest;
import com.kcmc.complaints.exception.NotFoundException;
import com.kcmc.complaints.exception.InvalidRoleException;
import com.kcmc.complaints.model.Department;
import com.kcmc.complaints.model.Role;
import com.kcmc.complaints.model.User;
import com.kcmc.complaints.repository.DepartmentRepository;
import com.kcmc.complaints.repository.RoleRepository;
import com.kcmc.complaints.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    private final UserRepository userRepo;
    private final RoleRepository roleRepo;
    private final DepartmentRepository deptRepo;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepo, RoleRepository roleRepo, DepartmentRepository deptRepo, PasswordEncoder passwordEncoder) {
        this.userRepo = userRepo;
        this.roleRepo = roleRepo;
        this.deptRepo = deptRepo;
        this.passwordEncoder = passwordEncoder;
    }

    public Page<User> findAll(Pageable pageable) {
        return userRepo.findAll(pageable);
    }

    public User findById(Long id) {
        return userRepo.findById(id).orElseThrow(() -> new NotFoundException("User not found: " + id));
    }

    @Transactional
    public User createStaffUser(CreateUserRequest req) {
        logger.info("Creating staff user for username: {}", req.username);

        // Validate and set role to STAFF
        Short staffRoleId = 1; // Assuming STAFF ID is 1
        Role staffRole = roleRepo.findById(staffRoleId)
                .orElseThrow(() -> new NotFoundException("STAFF role not found"));

        // Ignore any provided roleId and force STAFF
        req.roleId = staffRoleId;

        // Additional validation for self-registration
        if (userRepo.existsByUsername(req.username)) {
            throw new IllegalArgumentException("Username already exists");
        }
        if (userRepo.existsByEmail(req.email)) {
            throw new IllegalArgumentException("Email already exists");
        }
        if (req.employeeId != null && userRepo.existsByEmployeeId(req.employeeId)) {
            throw new IllegalArgumentException("Employee ID already exists");
        }

        return createUser(req, staffRole);
    }

    @Transactional
    public User create(CreateUserRequest req) {
        logger.info("Creating admin user for username: {}", req.username);

        if (req.roleId == null) {
            throw new IllegalArgumentException("Role ID is required for admin user creation");
        }

        Role role = roleRepo.findById(req.roleId)
                .orElseThrow(() -> new NotFoundException("Role not found: " + req.roleId));

        // Validate role for admin creation
        String roleName = role.getName();
        if (!"ADMINISTRATOR".equalsIgnoreCase(roleName) && !"ICT_AGENT".equalsIgnoreCase(roleName)) {
            throw new InvalidRoleException("Only ADMINISTRATOR and ICT_AGENT roles can be created by admins. Role: " + roleName);
        }

        // Additional validation
        if (userRepo.existsByUsername(req.username)) {
            throw new IllegalArgumentException("Username already exists");
        }
        if (userRepo.existsByEmail(req.email)) {
            throw new IllegalArgumentException("Email already exists");
        }
        if (req.employeeId != null && userRepo.existsByEmployeeId(req.employeeId)) {
            throw new IllegalArgumentException("Employee ID already exists");
        }

        return createUser(req, role);
    }

    private User createUser(CreateUserRequest req, Role role) {
        Department dept = (req.departmentId == null) ? null :
                deptRepo.findById(req.departmentId).orElseThrow(() -> new NotFoundException("Department not found: " + req.departmentId));

        User u = new User();
        u.setEmployeeId(req.employeeId);
        u.setUsername(req.username);
        u.setEmail(req.email);
        u.setPasswordHash(passwordEncoder.encode(req.password));
        u.setFirstName(req.firstName);
        u.setLastName(req.lastName);
        u.setRole(role);
        u.setDepartment(dept);

        User savedUser = userRepo.save(u);
        logger.info("User created successfully: ID={}, username={}, role={}",
                savedUser.getId(), savedUser.getUsername(), role.getName());
        return savedUser;
    }

    @Transactional
    public User update(Long id, UpdateUserRequest req, boolean isAdmin) {
        User user = userRepo.findById(id).orElseThrow(() -> new NotFoundException("User not found: " + id));

        // Validate duplicates (skip if unchanged)
        if (req.username != null && !req.username.equals(user.getUsername()) && userRepo.existsByUsername(req.username)) {
            throw new IllegalArgumentException("Username already exists");
        }
        if (req.email != null && !req.email.equals(user.getEmail()) && userRepo.existsByEmail(req.email)) {
            throw new IllegalArgumentException("Email already exists");
        }
        if (req.employeeId != null && !req.employeeId.equals(user.getEmployeeId()) && userRepo.existsByEmployeeId(req.employeeId)) {
            throw new IllegalArgumentException("Employee ID already exists");
        }

        // Update fields based on request
        if (req.employeeId != null) user.setEmployeeId(req.employeeId);
        if (req.username != null) user.setUsername(req.username);
        if (req.email != null) user.setEmail(req.email);
        if (req.password != null) user.setPasswordHash(passwordEncoder.encode(req.password));
        if (req.firstName != null) user.setFirstName(req.firstName);
        if (req.lastName != null) user.setLastName(req.lastName);

        // Admin-only fields
        if (isAdmin) {
            if (req.roleId != null) {
                Role role = roleRepo.findById(req.roleId)
                        .orElseThrow(() -> new NotFoundException("Role not found: " + req.roleId));
                user.setRole(role);
            }
            if (req.departmentId != null) {
                Department dept = deptRepo.findById(req.departmentId)
                        .orElseThrow(() -> new NotFoundException("Department not found: " + req.departmentId));
                user.setDepartment(dept);
            } else if (req.departmentId == null && user.getDepartment() != null) {
                user.setDepartment(null); // Allow clearing department
            }
            if (req.isActive != null) user.setIsActive(req.isActive);
        } else {
            // Non-admin users cannot update roleId, departmentId, or isActive
            if (req.roleId != null || req.departmentId != null || req.isActive != null) {
                throw new IllegalArgumentException("Non-admin users cannot update role, department, or active status");
            }
        }

        User updatedUser = userRepo.save(user);
        logger.info("User ID: {} updated successfully", id);
        return updatedUser;
    }

    @Transactional
    public void delete(Long id) {
        if (!userRepo.existsById(id)) {
            throw new NotFoundException("User not found: " + id);
        }
        logger.info("Deleting user ID: {}", id);

        userRepo.deleteById(id);
    }
}