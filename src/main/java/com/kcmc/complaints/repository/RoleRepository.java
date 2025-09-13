// RoleRepository.java
package com.kcmc.complaints.repository;

import com.kcmc.complaints.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Short> {
    Optional<Role> findByName(String name);
}
