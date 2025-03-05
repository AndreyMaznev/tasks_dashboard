package ru.effective.mobile.tasks_dashboard.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.effective.mobile.tasks_dashboard.dto.LoginRequest;
import ru.effective.mobile.tasks_dashboard.dto.RegisterRequest;
import ru.effective.mobile.tasks_dashboard.exception.UserNotFoundException;
import ru.effective.mobile.tasks_dashboard.model.User;
import ru.effective.mobile.tasks_dashboard.service.AuthService;
import ru.effective.mobile.tasks_dashboard.service.TokenService;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;
    private final TokenService tokenService;

    @Autowired
    public AuthController(AuthService authService, TokenService tokenService) {
        this.authService = authService;
        this.tokenService = tokenService;
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@RequestBody RegisterRequest request) {
        try {
            // Регистрация пользователя и генерация токенов
            Map<String, Object> response = authService.registerWithTokens(
                    request.getEmail(),
                    request.getPassword()
            );

            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody LoginRequest request) {
        User user = authService.authenticate(request.getEmail(), request.getPassword());
        return ResponseEntity.ok(tokenService.generateTokenPair(user.getEmail(), user.getRoles()));
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<Map<String, String>> refreshToken(@RequestBody Map<String, String> refreshTokenRequest) throws UserNotFoundException {
        String refreshToken = refreshTokenRequest.get("refresh_token");
        return ResponseEntity.ok(tokenService.refreshAccessToken(refreshToken));
    }
}
