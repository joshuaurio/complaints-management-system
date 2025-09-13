package com.kcmc.complaints.controller;

import com.kcmc.complaints.dto.CreateUserRequest;
import com.kcmc.complaints.dto.LoginRequest;
import com.kcmc.complaints.dto.LoginResponse;
import com.kcmc.complaints.model.User;
import com.kcmc.complaints.repository.UserRepository;
import com.kcmc.complaints.security.JwtTokenProvider;
import com.kcmc.complaints.service.ActivityLogService;
import com.kcmc.complaints.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserService userService;
    private final ActivityLogService activityLogService;

    public AuthController(AuthenticationManager authenticationManager,
                          UserRepository userRepository,
                          PasswordEncoder passwordEncoder,
                          JwtTokenProvider jwtTokenProvider,
                          UserService userService,
                          ActivityLogService activityLogService) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
        this.userService = userService;
        this.activityLogService = activityLogService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> authenticateUser(@RequestBody LoginRequest loginRequest,
                                                          HttpServletRequest httpRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.username, loginRequest.password)
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = jwtTokenProvider.generateToken(authentication);

        User user = userRepository.findByUsername(loginRequest.username);

        activityLogService.log(user.getId(), "LOGIN", "User logged in", httpRequest);

        LoginResponse response = new LoginResponse(
                token,
                user.getId(),
                user.getUsername(),
                user.getRole().getName(),
                user.getFirstName(),
                user.getLastName()
        );
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody CreateUserRequest req, HttpServletRequest httpRequest) {
        // Basic duplication checks
        if (userRepository.existsByUsername(req.username)) {
            return ResponseEntity.badRequest().body(null);
        }
        if (userRepository.existsByEmail(req.email)) {
            return ResponseEntity.badRequest().body(null);
        }
        User created = userService.create(req);
        activityLogService.log(created.getId(), "REGISTER", "User registered", httpRequest);
        return ResponseEntity.ok(created);
    }

    @GetMapping("/hash")
    public String hashPassword(@RequestParam String password) {
        return passwordEncoder.encode(password);
    }
}