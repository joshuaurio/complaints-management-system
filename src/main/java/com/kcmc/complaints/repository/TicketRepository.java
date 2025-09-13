package com.kcmc.complaints.repository;

import com.kcmc.complaints.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TicketRepository extends JpaRepository<Ticket, Long> {
    List<Ticket> findByStatus(TicketStatus status);
    List<Ticket> findByCreatedBy(User user);
    List<Ticket> findByAssignedAgent(User user);

    // ✅ New method to count tickets by status ID
    long countByStatusId(Short statusId);

    // ✅ New method for priorities
    List<Ticket> findByPriority(TicketPriority priority);

    List<Ticket> findByCategory(TicketCategory category);




}
