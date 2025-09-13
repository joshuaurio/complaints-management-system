package com.kcmc.complaints.controller;

import com.kcmc.complaints.dto.TicketCommentRequest;
import com.kcmc.complaints.model.TicketComment;
import com.kcmc.complaints.service.TicketCommentService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ticket-comments")
@CrossOrigin
public class TicketCommentController {
    private final TicketCommentService svc;

    public TicketCommentController(TicketCommentService svc) {
        this.svc = svc;
    }

    @GetMapping("/ticket/{ticketId}")
    public List<TicketComment> byTicket(@PathVariable Long ticketId) {
        return svc.findByTicket(ticketId);
    }

    @PostMapping
    public TicketComment add(@RequestBody @Valid TicketCommentRequest req) {
        return svc.add(req);
    }
}
