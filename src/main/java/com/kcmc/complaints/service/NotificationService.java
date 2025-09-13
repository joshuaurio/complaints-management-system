package com.kcmc.complaints.service;

import com.kcmc.complaints.model.Notification;
import com.kcmc.complaints.model.Ticket;
import com.kcmc.complaints.model.User;
import com.kcmc.complaints.repository.NotificationRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationService {
    private final NotificationRepository repo;
    private final LookupService lookup;

    public NotificationService(NotificationRepository repo, LookupService lookup) {
        this.repo = repo;
        this.lookup = lookup;
    }

    public List<Notification> unreadForUser(Long userId){
        return repo.findByUser_IdAndReadFalse(userId);
    }

    public Notification create(Long userId, Long ticketId, String message){
        User user = lookup.mustGetUser(userId);
        Ticket ticket = (ticketId == null) ? null : lookup.mustGetTicket(ticketId);
        Notification n = new Notification();
        n.setUser(user);
        n.setTicket(ticket);
        n.setMessage(message);
        return repo.save(n);
    }

    public void markRead(Long id){
        Notification n = repo.findById(id).orElseThrow();
        n.setRead(true);
        repo.save(n);
    }
}
