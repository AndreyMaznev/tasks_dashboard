package ru.effective.mobile.tasks_dashboard.service.implementations;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import ru.effective.mobile.tasks_dashboard.service.interfaces.TokenRedisService;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Service
public class TokenRedisServiceImpl implements TokenRedisService {

    private final StringRedisTemplate redisTemplate;
    private static final Logger logger = LoggerFactory.getLogger(TokenRedisServiceImpl.class);

    @Autowired
    public TokenRedisServiceImpl(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void saveRefreshToken(String refreshToken, String email, long expirationInMs) {
        logger.info("Сохранение refresh token: {}, email: {}, expiration: {}ms", refreshToken, email, expirationInMs);
        redisTemplate.opsForValue().set(refreshToken, email, Duration.ofMillis(expirationInMs));
    }

    public boolean isRefreshTokenValid(String refreshToken) {
        if (refreshToken == null || refreshToken.isEmpty()) {
            return false;
        }

        Boolean hasKey = redisTemplate.hasKey(refreshToken);
        if (!Boolean.TRUE.equals(hasKey)) {
            return false;
        }

        Long remainingTime = redisTemplate.getExpire(refreshToken, TimeUnit.SECONDS);
        return remainingTime != null && remainingTime > 0;
    }

    public String getUserEmailFromRefreshToken(String refreshToken) {
        return redisTemplate.opsForValue().get(refreshToken);
    }

    public void revokeRefreshToken(String refreshToken) {
        redisTemplate.delete(refreshToken);
    }

    public void markTokenAsUsed(String token) {
        redisTemplate.opsForSet().add("used_tokens", token);
    }

    public boolean isTokenUsed(String token) {
        if (token == null || token.isEmpty()) {
            return true;
        }
        Boolean isMember = redisTemplate.opsForSet().isMember("used_tokens", token);
        return Boolean.TRUE.equals(isMember);
    }
}