// AttachmentRepository.java
package com.kcmc.complaints.repository;

import com.kcmc.complaints.model.Attachment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AttachmentRepository extends JpaRepository<Attachment, Long> {
    List<Attachment> findByTicket_Id(Long ticketId);
}
