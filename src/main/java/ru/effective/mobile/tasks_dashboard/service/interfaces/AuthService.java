package ru.effective.mobile.tasks_dashboard.service.interfaces;

import ru.effective.mobile.tasks_dashboard.dto.auth.*;
import ru.effective.mobile.tasks_dashboard.model.User;

public interface AuthService {
    RegisterResponse registerWithTokens(RegisterRequest request);
    AuthResponse loginUser(LoginRequest request);
    RefreshResponse refreshAccessToken(String refreshToken);
    User authenticate(String email, String password);
}
