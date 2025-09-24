package com.kcmc.complaints.repository;

import com.kcmc.complaints.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    boolean existsByDepartmentId(Integer departmentId);
    boolean existsByEmployeeId(String employeeId);
}
