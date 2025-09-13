package com.kcmc.complaints.controller;

import com.kcmc.complaints.dto.CreateUserRequest;
import com.kcmc.complaints.model.User;
import com.kcmc.complaints.service.UserService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@CrossOrigin
public class UserController {
    private final UserService svc;

    public UserController(UserService svc) {
        this.svc = svc;
    }

    @GetMapping
    public Page<User> all(Pageable pageable) {
        return svc.findAll(pageable);
    }

    @GetMapping("/{id}")
    public User one(@PathVariable Long id) {
        return svc.findById(id);
    }

    @PostMapping
    public User create(@RequestBody @Valid CreateUserRequest req) {
        return svc.create(req);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        svc.delete(id);
    }
}
