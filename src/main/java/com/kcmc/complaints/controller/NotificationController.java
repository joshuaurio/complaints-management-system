// NotificationController.java
package com.kcmc.complaints.controller;

import com.kcmc.complaints.model.Notification;
import com.kcmc.complaints.service.NotificationService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@CrossOrigin
public class NotificationController {
    private final NotificationService svc;
    public NotificationController(NotificationService svc) { this.svc = svc; }

    @GetMapping("/user/{userId}")
    public List<Notification> unread(@PathVariable Long userId){ return svc.unreadForUser(userId); }

    @PostMapping("/{id}/read")
    public void markRead(@PathVariable Long id){ svc.markRead(id); }
}
