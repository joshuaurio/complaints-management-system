package com.kcmc.complaints.service;

import com.kcmc.complaints.dto.AssignAgentRequest;
import com.kcmc.complaints.dto.TicketRequest;
import com.kcmc.complaints.dto.UpdateTicketStatusRequest;
import com.kcmc.complaints.exception.NotFoundException;
import com.kcmc.complaints.model.*;
import com.kcmc.complaints.repository.TicketRepository;
import com.kcmc.complaints.repository.TicketStatusRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class TicketService {
    private final TicketRepository ticketRepo;
    private final LookupService lookup;
    private final TicketStatusRepository statusRepo;

    public TicketService(TicketRepository ticketRepo, LookupService lookup, TicketStatusRepository statusRepo) {
        this.ticketRepo = ticketRepo;
        this.lookup = lookup;
        this.statusRepo = statusRepo;
    }

    public Page<Ticket> findAll(Pageable pageable) {
        return ticketRepo.findAll(pageable);
    }

    public Ticket findById(Long id) {
        return ticketRepo.findById(id).orElseThrow(() -> new NotFoundException("Ticket not found: " + id));
    }

    @Transactional
    public Ticket create(TicketRequest req) {
        User creator = lookup.mustGetUser(req.createdById);
        User agent = (req.assignedAgentId == null) ? null : lookup.mustGetUser(req.assignedAgentId);
        TicketCategory category = lookup.mustGetCategory(req.categoryId);
        TicketPriority priority = lookup.mustGetPriority(req.priorityId);

        TicketStatus status;
        if (req.statusName != null && !req.statusName.isBlank()) {
            status = lookup.mustGetStatusByName(req.statusName);
        } else {
            status = lookup.mustGetStatusByName("OPEN");
        }

        Ticket t = new Ticket();
        t.setTitle(req.title);
        t.setDescription(req.description);
        t.setCreatedBy(creator);
        t.setAssignedAgent(agent);
        t.setCategory(category);
        t.setPriority(priority);
        t.setStatus(status);
        return ticketRepo.save(t);
    }

    @Transactional
    public Ticket updateStatus(Long ticketId, UpdateTicketStatusRequest req) {
        Ticket ticket = findById(ticketId);
        TicketStatus status = lookup.mustGetStatusByName(req.statusName);
        ticket.setStatus(status);
        if (Boolean.TRUE.equals(status.getResolvedStatus())) {
            ticket.setClosedAt(Instant.now());
        } else {
            ticket.setClosedAt(null); // Reset closedAt if status is not resolved
        }
        return ticketRepo.save(ticket);
    }

    @Transactional
    public Ticket assignAgent(Long ticketId, AssignAgentRequest req) {
        Ticket t = findById(ticketId);
        User agent = lookup.mustGetUser(req.agentId);
        t.setAssignedAgent(agent);
        return ticketRepo.save(t);
    }

    @Transactional
    public void delete(Long id) {
        ticketRepo.deleteById(id);
    }
}
