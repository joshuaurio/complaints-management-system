package com.kcmc.complaints.controller;

import com.kcmc.complaints.model.TicketStatus;
import com.kcmc.complaints.repository.TicketRepository;
import com.kcmc.complaints.repository.TicketStatusRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/statuses")
@CrossOrigin
public class TicketStatusController {
    private final TicketStatusRepository repo;
    private final TicketRepository ticketRepo;

    public TicketStatusController(TicketStatusRepository repo, TicketRepository ticketRepo) {
        this.repo = repo;
        this.ticketRepo = ticketRepo;
    }

    // ✅ Get all statuses
    @GetMapping
    public List<TicketStatus> all() {
        return repo.findAll();
    }

    // ✅ Get single status by ID
    @GetMapping("/{id}")
    public ResponseEntity<TicketStatus> getById(@PathVariable Short id) {
        return repo.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // ✅ Create status
    @PostMapping
    public ResponseEntity<?> create(@RequestBody TicketStatus status) {
        if (status.getName() == null || status.getName().isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Status name is required"));
        }

        if (repo.findByName(status.getName()).isPresent()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Status with this name already exists"));
        }

        TicketStatus saved = repo.save(status);
        return ResponseEntity.ok(saved);
    }

    // ✅ Update status
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Short id, @RequestBody TicketStatus updated) {
        return repo.findById(id).map(existing -> {
            if (updated.getName() == null || updated.getName().isBlank()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Status name is required"));
            }

            repo.findByName(updated.getName())
                    .filter(other -> !other.getId().equals(id))
                    .ifPresent(conflict -> {
                        throw new RuntimeException("Another status with this name already exists");
                    });

            existing.setName(updated.getName());
            existing.setDescription(updated.getDescription());
            existing.setResolvedStatus(updated.getResolvedStatus());

            TicketStatus saved = repo.save(existing);
            return ResponseEntity.ok(saved);
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // ✅ Delete status
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Short id) {
        return repo.findById(id).map(existing -> {
            // ❌ Prevent delete if status is used by tickets
            long count = ticketRepo.countByStatusId(id);
            if (count > 0) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Cannot delete status. It is assigned to " + count + " tickets."));
            }

            repo.delete(existing);
            return ResponseEntity.ok(Map.of("message", "Status deleted successfully"));
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }
}
