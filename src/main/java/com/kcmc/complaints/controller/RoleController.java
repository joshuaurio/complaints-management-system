package com.kcmc.complaints.controller;

import com.kcmc.complaints.model.Role;
import com.kcmc.complaints.repository.RoleRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/roles")
@CrossOrigin
public class RoleController {
    private final RoleRepository repo;
    public RoleController(RoleRepository repo) { this.repo = repo; }

    @GetMapping
    public List<Role> all(){ return repo.findAll(); }
}
