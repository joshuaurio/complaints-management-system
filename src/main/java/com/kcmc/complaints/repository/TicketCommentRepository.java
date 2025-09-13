// TicketCommentRepository.java
package com.kcmc.complaints.repository;

import com.kcmc.complaints.model.TicketComment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TicketCommentRepository extends JpaRepository<TicketComment, Long> {
    List<TicketComment> findByTicket_IdOrderByCreatedAtAsc(Long ticketId);
}
