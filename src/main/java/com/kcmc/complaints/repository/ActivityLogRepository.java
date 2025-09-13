package com.kcmc.complaints.repository;

import com.kcmc.complaints.model.ActivityLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ActivityLogRepository extends JpaRepository<ActivityLog, Long> { }
