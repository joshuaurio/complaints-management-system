// TicketStatusRepository.java
package com.kcmc.complaints.repository;

import com.kcmc.complaints.model.TicketStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface TicketStatusRepository extends JpaRepository<TicketStatus, Short> {
    Optional<TicketStatus> findByName(String name);
}
