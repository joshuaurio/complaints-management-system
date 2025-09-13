package com.kcmc.complaints.service;

import com.kcmc.complaints.dto.CreateUserRequest;
import com.kcmc.complaints.exception.NotFoundException;
import com.kcmc.complaints.model.Department;
import com.kcmc.complaints.model.Role;
import com.kcmc.complaints.model.User;
import com.kcmc.complaints.repository.DepartmentRepository;
import com.kcmc.complaints.repository.RoleRepository;
import com.kcmc.complaints.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
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
    public User create(CreateUserRequest req) {
        Role role = roleRepo.findById(req.roleId).orElseThrow(() -> new NotFoundException("Role not found: " + req.roleId));
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
        return userRepo.save(u);
    }

    @Transactional
    public void delete(Long id) {
        userRepo.deleteById(id);
    }
}
