package com.kcmc.complaints.controller;

import com.kcmc.complaints.model.ActivityLog;
import com.kcmc.complaints.repository.ActivityLogRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/activity-logs")
@CrossOrigin
public class ActivityLogController {

    private final ActivityLogRepository repo;

    public ActivityLogController(ActivityLogRepository repo) {
        this.repo = repo;
    }

    @GetMapping
    public Page<ActivityLog> all(Pageable pageable) {
        return repo.findAll(pageable);
    }
}
