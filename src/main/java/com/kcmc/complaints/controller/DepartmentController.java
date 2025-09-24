package com.kcmc.complaints.controller;

import com.kcmc.complaints.model.Department;
import com.kcmc.complaints.repository.DepartmentRepository;
import com.kcmc.complaints.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/departments")
@CrossOrigin
public class DepartmentController {
    private static final Logger logger = LoggerFactory.getLogger(DepartmentController.class);
    private final DepartmentRepository repo;
    private final UserRepository userRepo; // Added UserRepository

    public DepartmentController(DepartmentRepository repo, UserRepository userRepo) {
        this.repo = repo;
        this.userRepo = userRepo;
    }

    // Structured error response
    private ResponseEntity<Map<String, Object>> errorResponse(String errorCode, String message, int status) {
        Map<String, Object> error = new HashMap<>();
        error.put("errorCode", errorCode);
        error.put("message", message);
        return ResponseEntity.status(status).body(error);
    }

    // ✅ Get all departments
    @GetMapping
    @Transactional(readOnly = true)
    public ResponseEntity<?> all() {
        try {
            logger.info("Fetching all departments");
            List<Department> departments = repo.findAll();
            if (departments.isEmpty()) {
                return ResponseEntity.ok().body(Map.of("message", "No departments found", "data", departments));
            }
            return ResponseEntity.ok(departments);
        } catch (Exception e) {
            logger.error("Error fetching departments", e);
            return errorResponse("FETCH_FAILED", "An error occurred while fetching departments", 500);
        }
    }

    // ✅ Get department by ID
    @GetMapping("/{id}")
    @Transactional(readOnly = true)
    public ResponseEntity<?> getById(@PathVariable Integer id) {
        try {
            if (id == null || id <= 0) {
                return errorResponse("INVALID_ID", "Department ID must be a positive integer", 400);
            }
            logger.info("Fetching department with ID: {}", id);
            Optional<Department> department = repo.findById(id);
            if (department.isEmpty()) {
                return errorResponse("NOT_FOUND", "Department not found with ID: " + id, 404);
            }
            return ResponseEntity.ok(department.get());
        } catch (Exception e) {
            logger.error("Error fetching department with ID: {}", id, e);
            return errorResponse("FETCH_FAILED", "An error occurred while fetching the department", 500);
        }
    }

    // ✅ Create department
    @PostMapping
    @Transactional
    public ResponseEntity<?> create(@RequestBody Department department) {
        try {
            // Validate input
            if (department == null) {
                return errorResponse("INVALID_INPUT", "Department data is required", 400);
            }
            if (department.getName() == null || department.getName().trim().isEmpty()) {
                return errorResponse("INVALID_NAME", "Department name is required", 400);
            }
            if (department.getName().length() > 100) {
                return errorResponse("INVALID_NAME", "Department name must not exceed 100 characters", 400);
            }
            if (department.getDescription() != null && department.getDescription().length() > 1000) {
                return errorResponse("INVALID_DESCRIPTION", "Description must not exceed 1000 characters", 400);
            }
            if (repo.existsByName(department.getName())) {
                return errorResponse("DUPLICATE_NAME", "Department with name '" + department.getName() + "' already exists", 400);
            }

            logger.info("Creating department: {}", department.getName());
            Department saved = repo.save(department);
            return ResponseEntity.ok(saved);
        } catch (Exception e) {
            logger.error("Error creating department: {}", department.getName(), e);
            return errorResponse("CREATE_FAILED", "An error occurred while creating the department", 500);
        }
    }

    // ✅ Update department
    @PutMapping("/{id}")
    @Transactional
    public ResponseEntity<?> update(@PathVariable Integer id, @RequestBody Department department) {
        try {
            if (id == null || id <= 0) {
                return errorResponse("INVALID_ID", "Department ID must be a positive integer", 400);
            }
            if (department == null) {
                return errorResponse("INVALID_INPUT", "Department data is required", 400);
            }
            if (department.getName() == null || department.getName().trim().isEmpty()) {
                return errorResponse("INVALID_NAME", "Department name is required", 400);
            }
            if (department.getName().length() > 100) {
                return errorResponse("INVALID_NAME", "Department name must not exceed 100 characters", 400);
            }
            if (department.getDescription() != null && department.getDescription().length() > 1000) {
                return errorResponse("INVALID_DESCRIPTION", "Description must not exceed 1000 characters", 400);
            }

            Optional<Department> existingOpt = repo.findById(id);
            if (existingOpt.isEmpty()) {
                return errorResponse("NOT_FOUND", "Department not found with ID: " + id, 404);
            }

            Department existing = existingOpt.get();

            // Check for duplicate name
            Optional<Department> duplicate = repo.findByName(department.getName());
            if (duplicate.isPresent() && !duplicate.get().getId().equals(id)) {
                return errorResponse("DUPLICATE_NAME", "Another department with name '" + department.getName() + "' already exists", 400);
            }

            logger.info("Updating department ID: {} with name: {}", id, department.getName());
            existing.setName(department.getName());
            existing.setDescription(department.getDescription());

            Department updated = repo.save(existing);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            logger.error("Error updating department ID: {}", id, e);
            return errorResponse("UPDATE_FAILED", "An error occurred while updating the department", 500);
        }
    }

    // ✅ Delete department
    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<?> delete(@PathVariable Integer id) {
        try {
            if (id == null || id <= 0) {
                return errorResponse("INVALID_ID", "Department ID must be a positive integer", 400);
            }
            Optional<Department> existingOpt = repo.findById(id);
            if (existingOpt.isEmpty()) {
                return errorResponse("NOT_FOUND", "Department not found with ID: " + id, 404);
            }

            // Check if any users are associated with this department
            if (userRepo.existsByDepartmentId(id)) {
                return errorResponse("FOREIGN_KEY_CONSTRAINT",
                        "Cannot delete department with ID: " + id + " because it is associated with one or more users", 400);
            }

            logger.info("Deleting department ID: {}", id);
            repo.deleteById(id);
            return ResponseEntity.ok(Map.of("message", "Department deleted successfully"));
        } catch (Exception e) {
            logger.error("Error deleting department ID: {}", id, e);
            return errorResponse("DELETE_FAILED", "An error occurred while deleting the department", 500);
        }
    }
}