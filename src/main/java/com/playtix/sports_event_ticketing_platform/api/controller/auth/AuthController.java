package com.playtix.sports_event_ticketing_platform.api.controller.auth;

import com.playtix.sports_event_ticketing_platform.domain.dto.auth.AuthResponse;
import com.playtix.sports_event_ticketing_platform.domain.dto.auth.LoginRequest;
import com.playtix.sports_event_ticketing_platform.service.auth.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }
}