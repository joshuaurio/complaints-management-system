package com.kcmc.complaints.controller;

import com.kcmc.complaints.model.Ticket;
import com.kcmc.complaints.model.TicketPriority;
import com.kcmc.complaints.repository.TicketPriorityRepository;
import com.kcmc.complaints.repository.TicketRepository;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/priorities")
@CrossOrigin
public class TicketPriorityController {
    private final TicketPriorityRepository repo;
    private final TicketRepository ticketRepo;

    public TicketPriorityController(TicketPriorityRepository repo, TicketRepository ticketRepo) {
        this.repo = repo;
        this.ticketRepo = ticketRepo;
    }

    // ✅ Get all priorities
    @GetMapping
    public List<TicketPriority> all() {
        return repo.findAll();
    }

    // ✅ Create new priority
    @PostMapping
    public TicketPriority create(@Valid @RequestBody TicketPriority priority) {
        repo.findByName(priority.getName()).ifPresent(p -> {
            throw new IllegalArgumentException("Priority already exists: " + priority.getName());
        });
        return repo.save(priority);
    }

    // ✅ Update priority
    @PutMapping("/{id}")
    public ResponseEntity<TicketPriority> update(@PathVariable Short id, @Valid @RequestBody TicketPriority priority) {
        return repo.findById(id)
                .map(existing -> {
                    existing.setName(priority.getName());
                    existing.setDescription(priority.getDescription());
                    return ResponseEntity.ok(repo.save(existing));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // ✅ Delete priority (only if not used in tickets)
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Short id) {
        return repo.findById(id)
                .map(priority -> {
                    List<Ticket> ticketsUsing = ticketRepo.findByPriority(priority);
                    if (!ticketsUsing.isEmpty()) {
                        return ResponseEntity.badRequest()
                                .body("❌ Cannot delete priority because it is used by one or more tickets.");
                    }
                    repo.delete(priority);
                    return ResponseEntity.ok("✅ Priority deleted successfully.");
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
