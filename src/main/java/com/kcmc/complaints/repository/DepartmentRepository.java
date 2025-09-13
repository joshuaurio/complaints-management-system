// DepartmentRepository.java
package com.kcmc.complaints.repository;

import com.kcmc.complaints.model.Department;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DepartmentRepository extends JpaRepository<Department, Integer> {
    Optional<Department> findByName(String name);
    boolean existsByName(String name);
}
