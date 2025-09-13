// DepartmentController.java
package com.kcmc.complaints.controller;

import com.kcmc.complaints.model.Department;
import com.kcmc.complaints.repository.DepartmentRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/departments")
@CrossOrigin
public class DepartmentController {
    private final DepartmentRepository repo;

    public DepartmentController(DepartmentRepository repo) {
        this.repo = repo;
    }

    // ✅ Get all departments
    @GetMapping
    public List<Department> all() {
        return repo.findAll();
    }

    // ✅ Create department
    @PostMapping
    public ResponseEntity<?> create(@RequestBody Department department) {
        if (department.getName() == null || department.getName().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Department name is required.");
        }
        if (repo.existsByName(department.getName())) {
            return ResponseEntity.badRequest().body("Department with this name already exists.");
        }

        Department saved = repo.save(department);
        return ResponseEntity.ok(saved);
    }

    // ✅ Update department
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Integer id, @RequestBody Department department) {
        Optional<Department> existingOpt = repo.findById(id);
        if (existingOpt.isEmpty()) {
            return ResponseEntity.status(404).body("Department not found with ID " + id);
        }

        Department existing = existingOpt.get();

        if (department.getName() == null || department.getName().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Department name is required.");
        }

        // Check duplicate name for other departments
        Optional<Department> duplicate = repo.findByName(department.getName());
        if (duplicate.isPresent() && !duplicate.get().getId().equals(id)) {
            return ResponseEntity.badRequest().body("Another department with this name already exists.");
        }

        existing.setName(department.getName());
        existing.setDescription(department.getDescription());

        Department updated = repo.save(existing);
        return ResponseEntity.ok(updated);
    }

    // ✅ Delete department
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Integer id) {
        Optional<Department> existingOpt = repo.findById(id);
        if (existingOpt.isEmpty()) {
            return ResponseEntity.status(404).body("Department not found with ID " + id);
        }

        repo.deleteById(id);
        return ResponseEntity.ok("Department deleted successfully.");
    }
}
