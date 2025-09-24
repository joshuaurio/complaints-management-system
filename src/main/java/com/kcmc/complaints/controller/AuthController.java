package com.kcmc.complaints.controller;

import com.kcmc.complaints.dto.LoginRequest;
import com.kcmc.complaints.dto.LoginResponse;
import com.kcmc.complaints.model.User;
import com.kcmc.complaints.repository.UserRepository;
import com.kcmc.complaints.security.JwtTokenProvider;
import com.kcmc.complaints.service.ActivityLogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin
public class AuthController {
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final ActivityLogService activityLogService;

    public AuthController(AuthenticationManager authenticationManager,
                          UserRepository userRepository,
                          JwtTokenProvider jwtTokenProvider,
                          ActivityLogService activityLogService) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.jwtTokenProvider = jwtTokenProvider;
        this.activityLogService = activityLogService;
    }

    // Structured error response
    private ResponseEntity<Map<String, Object>> errorResponse(String errorCode, String message, int status) {
        Map<String, Object> error = new HashMap<>();
        error.put("errorCode", errorCode);
        error.put("message", message);
        return ResponseEntity.status(status).body(error);
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody @Valid LoginRequest loginRequest, HttpServletRequest httpRequest) {
        try {
            logger.info("Login attempt for username: {}", loginRequest.username);
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.username, loginRequest.password)
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);
            String token = jwtTokenProvider.generateToken(authentication);

            User user = userRepository.findByUsername(loginRequest.username);
            if (user == null) {
                logger.error("User not found after authentication: {}", loginRequest.username);
                return errorResponse("LOGIN_FAILED", "User not found", 401);
            }

            activityLogService.log(user.getId(), "LOGIN", "User logged in", httpRequest);

            LoginResponse response = new LoginResponse(
                    token,
                    user.getId(),
                    user.getUsername(),
                    user.getRole().getName(),
                    user.getFirstName(),
                    user.getLastName()
            );
            logger.info("Login successful for username: {}", loginRequest.username);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Login failed for username: {}", loginRequest.username, e);
            return errorResponse("LOGIN_FAILED", "Invalid username or password", 401);
        }
    }

    // Removed /register endpoint - use /api/users/register or /api/users instead
}