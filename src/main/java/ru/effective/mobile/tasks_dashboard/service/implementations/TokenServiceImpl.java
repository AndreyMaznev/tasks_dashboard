package ru.effective.mobile.tasks_dashboard.service.implementations;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.effective.mobile.tasks_dashboard.exception.IllegalJwtTokenException;
import ru.effective.mobile.tasks_dashboard.exception.UserNotFoundException;
import ru.effective.mobile.tasks_dashboard.model.Role;
import ru.effective.mobile.tasks_dashboard.security.JwtConfig;
import ru.effective.mobile.tasks_dashboard.service.interfaces.TokenService;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Service
public class TokenServiceImpl implements TokenService {

    private final JwtConfig jwtConfig;
    private final TokenRedisServiceImpl tokenRedisServiceImpl;
    private static final Logger logger = LoggerFactory.getLogger(TokenServiceImpl.class);
    private final UserServiceImpl userServiceImpl;

    @Autowired
    public TokenServiceImpl(JwtConfig jwtConfig, TokenRedisServiceImpl tokenRedisServiceImpl, UserServiceImpl userServiceImpl) {
        this.jwtConfig = jwtConfig;
        this.tokenRedisServiceImpl = tokenRedisServiceImpl;
        this.userServiceImpl = userServiceImpl;
    }

    public Map<String, String> generateTokenPair(String email, Set<Role> roles) {
        logger.info("Generating token pair: email: {}", email);
        String accessToken = jwtConfig.generateAccessToken(email, userServiceImpl.getConvertedRoles(roles));
        String refreshToken = jwtConfig.generateRefreshToken(email);

        // Сохраняем Refresh Token в Redis
        tokenRedisServiceImpl.saveRefreshToken(
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
        if (!tokenRedisServiceImpl.isRefreshTokenValid(refreshToken)) {
            throw new IllegalJwtTokenException("Некорректный или просроченный токен.");
        }
        if (tokenRedisServiceImpl.isTokenUsed(refreshToken)) {
            throw new IllegalJwtTokenException("Токен уже использовался.");
        }
        String email = tokenRedisServiceImpl.getUserEmailFromRefreshToken(refreshToken);
        if (email == null || email.isEmpty()) {
            throw new IllegalJwtTokenException("Некорректный email в токене.");
        }

        Set<String> roles = userServiceImpl.getConvertedRoles(userServiceImpl.getUserRolesByEmail(email));
        String newAccessToken = jwtConfig.generateAccessToken(email, roles);
        String newRefreshToken = jwtConfig.generateRefreshToken(email);

        tokenRedisServiceImpl.saveRefreshToken(
                newRefreshToken,
                email,
                jwtConfig.getRefreshExpirationInMs()
        );

        //used + revoke в т.ч. для сценариев утечки и удаления из Redis.
        tokenRedisServiceImpl.markTokenAsUsed(refreshToken);
        tokenRedisServiceImpl.revokeRefreshToken(refreshToken);
        Map<String, String> tokenMap = new HashMap<>();
        tokenMap.put("access_token", newAccessToken);
        tokenMap.put("refresh_token", newRefreshToken);
        return tokenMap;
    }
}