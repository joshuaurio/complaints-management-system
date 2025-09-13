package com.kcmc.complaints.service;

import com.kcmc.complaints.dto.TicketCommentRequest;
import com.kcmc.complaints.model.Ticket;
import com.kcmc.complaints.model.TicketComment;
import com.kcmc.complaints.model.User;
import com.kcmc.complaints.repository.TicketCommentRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TicketCommentService {
    private final TicketCommentRepository commentRepo;
    private final LookupService lookup;

    public TicketCommentService(TicketCommentRepository commentRepo, LookupService lookup) {
        this.commentRepo = commentRepo;
        this.lookup = lookup;
    }

    public List<TicketComment> findByTicket(Long ticketId){
        return commentRepo.findByTicket_IdOrderByCreatedAtAsc(ticketId);
    }

    public TicketComment add(TicketCommentRequest req){
        Ticket ticket = lookup.mustGetTicket(req.ticketId);
        User author = lookup.mustGetUser(req.authorId);

        TicketComment c = new TicketComment();
        c.setTicket(ticket);
        c.setAuthor(author);
        c.setContent(req.content);
        c.setInternal(Boolean.TRUE.equals(req.isInternal));
        return commentRepo.save(c);
    }
}
