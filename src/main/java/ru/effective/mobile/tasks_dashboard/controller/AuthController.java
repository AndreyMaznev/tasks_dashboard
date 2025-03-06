package ru.effective.mobile.tasks_dashboard.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.effective.mobile.tasks_dashboard.dto.*;
import ru.effective.mobile.tasks_dashboard.exception.UserNotFoundException;
import ru.effective.mobile.tasks_dashboard.model.User;
import ru.effective.mobile.tasks_dashboard.service.AuthService;
import ru.effective.mobile.tasks_dashboard.service.TokenService;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.registerWithTokens(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.loginUser(request));
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<RefreshResponse> refreshToken(@RequestBody Map<String, String> refreshTokenRequest) {
        return ResponseEntity.ok(authService.refreshToken(refreshTokenRequest.get("refresh_token")));
    }
}