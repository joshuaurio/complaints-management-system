package com.kcmc.complaints.controller;

import com.kcmc.complaints.dto.AssignAgentRequest;
import com.kcmc.complaints.dto.TicketRequest;
import com.kcmc.complaints.dto.UpdateTicketStatusRequest;
import com.kcmc.complaints.model.Ticket;
import com.kcmc.complaints.service.TicketService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tickets")
@CrossOrigin
public class TicketController {
    private final TicketService svc;

    public TicketController(TicketService svc) {
        this.svc = svc;
    }

    @GetMapping
    public Page<Ticket> all(Pageable pageable) {
        return svc.findAll(pageable);
    }

    @GetMapping("/{id}")
    public Ticket one(@PathVariable Long id) {
        return svc.findById(id);
    }

    @PostMapping
    public Ticket create(@RequestBody @Valid TicketRequest req) {
        return svc.create(req);
    }

    @PutMapping("/{id}/status")
    public Ticket updateStatus(@PathVariable Long id, @RequestBody @Valid UpdateTicketStatusRequest req) {
        return svc.updateStatus(id, req);
    }

    @PutMapping("/{id}/assign")
    public Ticket assign(@PathVariable Long id, @RequestBody @Valid AssignAgentRequest req) {
        return svc.assignAgent(id, req);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        svc.delete(id);
    }
}
