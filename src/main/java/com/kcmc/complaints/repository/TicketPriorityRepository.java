// TicketPriorityRepository.java
package com.kcmc.complaints.repository;

import com.kcmc.complaints.model.TicketPriority;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface TicketPriorityRepository extends JpaRepository<TicketPriority, Short> {
    Optional<TicketPriority> findByName(String name);
}
