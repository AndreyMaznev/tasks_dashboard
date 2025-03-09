package ru.effective.mobile.tasks_dashboard.service.interfaces;

public interface TokenRedisService {
    void saveRefreshToken(String refreshToken, String email, long expirationInMs);
    boolean isRefreshTokenValid(String refreshToken);
    String getUserEmailFromRefreshToken(String refreshToken);
    void revokeRefreshToken(String refreshToken);
    void markTokenAsUsed(String token);
    boolean isTokenUsed(String token);
}
