package ru.effective.mobile.tasks_dashboard.service.interfaces;

import ru.effective.mobile.tasks_dashboard.model.Role;
import java.util.Map;
import java.util.Set;

public interface TokenService {
    Map<String, String> generateTokenPair(String email, Set<Role> roles);
    Map<String, String>  refreshAccessToken(String refreshToken);
}
