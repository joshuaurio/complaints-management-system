// NotificationRepository.java
package com.kcmc.complaints.repository;

import com.kcmc.complaints.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByUser_IdAndReadFalse(Long userId);
}

