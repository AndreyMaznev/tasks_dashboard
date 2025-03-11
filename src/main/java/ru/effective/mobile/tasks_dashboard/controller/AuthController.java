package ru.effective.mobile.tasks_dashboard.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.effective.mobile.tasks_dashboard.dto.auth.*;
import ru.effective.mobile.tasks_dashboard.service.implementations.AuthServiceImpl;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthServiceImpl authServiceImpl;

    public AuthController(AuthServiceImpl authServiceImpl) {
        this.authServiceImpl = authServiceImpl;
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authServiceImpl.registerWithTokens(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authServiceImpl.loginUser(request));
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<RefreshResponse> refreshAccessToken(@RequestBody Map<String, String> refreshTokenRequest) {
        return ResponseEntity.ok(authServiceImpl.refreshAccessToken(refreshTokenRequest.get("refresh_token")));
    }
}