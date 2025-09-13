package com.kcmc.complaints.controller;

import com.kcmc.complaints.model.TicketCategory;
import com.kcmc.complaints.model.Ticket;
import com.kcmc.complaints.repository.TicketCategoryRepository;
import com.kcmc.complaints.repository.TicketRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/categories")
@CrossOrigin
public class TicketCategoryController {
    private final TicketCategoryRepository repo;
    private final TicketRepository ticketRepo;

    public TicketCategoryController(TicketCategoryRepository repo, TicketRepository ticketRepo) {
        this.repo = repo;
        this.ticketRepo = ticketRepo;
    }

    // ✅ Get all categories
    @GetMapping
    public List<TicketCategory> all() {
        return repo.findAll();
    }

    // ✅ Create category
    @PostMapping
    public ResponseEntity<?> create(@RequestBody TicketCategory c) {
        if (c.getName() == null || c.getName().isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Category name is required"));
        }

        repo.findByName(c.getName()).ifPresent(existing -> {
            throw new IllegalArgumentException("Category already exists: " + c.getName());
        });

        TicketCategory saved = repo.save(c);
        return ResponseEntity.ok(saved);
    }

    // ✅ Update category
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Integer id, @RequestBody TicketCategory c) {
        return repo.findById(id).map(existing -> {
            if (c.getName() == null || c.getName().isBlank()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Category name is required"));
            }

            // prevent duplicate names
            repo.findByName(c.getName())
                    .filter(other -> !other.getId().equals(id))
                    .ifPresent(conflict -> {
                        throw new IllegalArgumentException("Another category with this name already exists");
                    });

            existing.setName(c.getName());
            existing.setDescription(c.getDescription());
            TicketCategory updated = repo.save(existing);
            return ResponseEntity.ok(updated);
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // ✅ Delete category (prevent if in use)
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Integer id) {
        return repo.findById(id).map(existing -> {
            List<Ticket> tickets = ticketRepo.findByCategory(existing);
            if (!tickets.isEmpty()) {
                return ResponseEntity.badRequest().body(
                        Map.of("error", "Cannot delete category. It is already used by tickets")
                );
            }

            repo.delete(existing);
            return ResponseEntity.ok(Map.of("message", "Category deleted successfully"));
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }
}
