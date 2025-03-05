package ru.effective.mobile.tasks_dashboard.service;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.effective.mobile.tasks_dashboard.exception.UserNotFoundException;
import ru.effective.mobile.tasks_dashboard.model.Role;
import ru.effective.mobile.tasks_dashboard.security.JwtConfig;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Service
public class TokenService {

    private final JwtConfig jwtConfig;
    private final TokenRedisService tokenRedisService;
    private static final Logger logger = LoggerFactory.getLogger(TokenService.class);
    private final UserService userService;

    @Autowired
    public TokenService(JwtConfig jwtConfig, TokenRedisService tokenRedisService, UserService userService) {
        this.jwtConfig = jwtConfig;
        this.tokenRedisService = tokenRedisService;
        this.userService = userService;
    }

    public Map<String, String> generateTokenPair(String email, Set<Role> roles) {
        logger.info("Generating token pair: email: {}", email);
        String accessToken = jwtConfig.generateAccessToken(email, userService.getConvertedRoles(roles));
        String refreshToken = jwtConfig.generateRefreshToken(email);

        // Сохраняем Refresh Token в Redis
        tokenRedisService.saveRefreshToken(
                refreshToken,
                email,
                jwtConfig.getRefreshExpirationInMs()
        );

        Map<String, String> tokenMap = new HashMap<>();
        tokenMap.put("access_token", accessToken);
        tokenMap.put("refresh_token", refreshToken);
        return tokenMap;
    }

    public Map<String, String>  refreshAccessToken(String refreshToken) throws UserNotFoundException {
        // Проверяем валидность Refresh Token
        if (!tokenRedisService.isRefreshTokenValid(refreshToken)) {
            throw new IllegalArgumentException("Invalid or expired refresh token");
        }

        // Проверяем, не был ли токен уже использован
        if (tokenRedisService.isTokenUsed(refreshToken)) {
            throw new IllegalArgumentException("Token has already been used");
        }

        // Получаем email пользователя из Redis
        String email = tokenRedisService.getUserEmailFromRefreshToken(refreshToken);
        if (email == null || email.isEmpty()) {
            throw new IllegalArgumentException("Invalid email in refresh token");
        }

        Set<String> roles = userService.getConvertedRoles(userService.getUserRolesByEmail(email));
        String newAccessToken = jwtConfig.generateAccessToken(email, roles);
        String newRefreshToken = jwtConfig.generateRefreshToken(email);


        tokenRedisService.saveRefreshToken(
                newRefreshToken,
                email,
                jwtConfig.getRefreshExpirationInMs()
        );

        //used + revoke в т.ч. для сценариев утечки и удаления из Redis.
        tokenRedisService.markTokenAsUsed(refreshToken);
        tokenRedisService.revokeRefreshToken(refreshToken);

        Map<String, String> tokenMap = new HashMap<>();
        tokenMap.put("access_token", newAccessToken);
        tokenMap.put("refresh_token", newRefreshToken);

        return tokenMap;
    }
}