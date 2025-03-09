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

    // Сохранение Refresh Token в Redis
    public void saveRefreshToken(String refreshToken, String email, long expirationInMs) {
        logger.info("Saving refresh token: {}, email: {}, expiration: {}ms", refreshToken, email, expirationInMs);
        redisTemplate.opsForValue().set(refreshToken, email, Duration.ofMillis(expirationInMs));
    }

    // Проверка существования Refresh Token в Redis
    //todo - упростить проверки в методе
    public boolean isRefreshTokenValid(String refreshToken) {
        if (refreshToken == null || refreshToken.isEmpty()) {
            return false; // Если токен null или пустой, считаем его недействительным
        }

        Boolean hasKey = redisTemplate.hasKey(refreshToken);
        //Сравнение с TRUE если Redis вернет null
        if (!Boolean.TRUE.equals(hasKey)) {
            return false; // Ключ не существует
        }

        Long remainingTime = redisTemplate.getExpire(refreshToken, TimeUnit.SECONDS);
        return remainingTime != null && remainingTime > 0; // Срок действия истек
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