package com.kcmc.complaints.service;

import com.kcmc.complaints.model.ActivityLog;
import com.kcmc.complaints.model.User;
import com.kcmc.complaints.repository.ActivityLogRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

@Service
public class ActivityLogService {

    private final ActivityLogRepository repo;
    private final LookupService lookup;

    public ActivityLogService(ActivityLogRepository repo, LookupService lookup) {
        this.repo = repo;
        this.lookup = lookup;
    }

    public ActivityLog log(Long userId, String action, String description, HttpServletRequest req) {
        User user = lookup.mustGetUser(userId);
        ActivityLog l = new ActivityLog();
        l.setUser(user);
        l.setAction(action);
        l.setDescription(description);
        if (req != null) {
            l.setIpAddress(req.getRemoteAddr());
            l.setUserAgent(req.getHeader("User-Agent"));
        }
        return repo.save(l);
    }
}
