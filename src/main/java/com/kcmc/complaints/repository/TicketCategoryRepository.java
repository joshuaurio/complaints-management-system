// TicketCategoryRepository.java
package com.kcmc.complaints.repository;

import com.kcmc.complaints.model.TicketCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface TicketCategoryRepository extends JpaRepository<TicketCategory, Integer> {
    Optional<TicketCategory> findByName(String name);
}
