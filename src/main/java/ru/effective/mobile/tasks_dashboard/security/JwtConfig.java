package ru.effective.mobile.tasks_dashboard.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import ru.effective.mobile.tasks_dashboard.exception.AccessRefusedException;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Configuration
public class JwtConfig {

    private final SecretKey accessKey;
    private final SecretKey refreshKey;
    private final long accessExpirationInMs;
    private final long refreshExpirationInMs;
    private static final Logger logger = LoggerFactory.getLogger(JwtConfig.class);

    @Autowired
    public JwtConfig(
            @Value("${jwt.access-secret}") String accessSecret,
            @Value("${jwt.refresh-secret}") String refreshSecret,
            @Value("${jwt.access-expiration-in-ms}") long accessExpirationInMs,
            @Value("${jwt.refresh-expiration-in-ms}") long refreshExpirationInMs) {
        this.accessKey = generateKey(accessSecret);
        this.refreshKey = generateKey(refreshSecret);
        this.accessExpirationInMs = accessExpirationInMs;
        this.refreshExpirationInMs = refreshExpirationInMs;
    }


    private SecretKey generateKey(String secret) {
        return new SecretKeySpec(secret.getBytes(), SignatureAlgorithm.HS512.getJcaName());
    }

    public SecretKey getAccessKey() {
        return accessKey;
    }

    public SecretKey getRefreshKey() {
        return refreshKey;
    }

    public long getAccessExpirationInMs() {
        return accessExpirationInMs;
    }

    public long getRefreshExpirationInMs() {
        return refreshExpirationInMs;
    }

    // Генерация Access Token
    public String generateAccessToken(String email, Set<String> roles) {
        return generateToken(email, roles, accessKey, accessExpirationInMs);
    }

    // Генерация Refresh Token
    public String generateRefreshToken(String username) {
        return generateToken(username, null, refreshKey, refreshExpirationInMs);
    }

    private String generateToken(String email, Set<String> roles, SecretKey key, long expirationInMs) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expirationInMs);

        return Jwts.builder()
                .setSubject(email)
                .claim("roles", roles)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }

    // Проверка Access Token
    public boolean validateAccessToken(String token) {
        return validateToken(token, accessKey);
    }

    private boolean validateToken(String token, SecretKey key) {
        try {
            Jwts.parser()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            logger.warn("Некорректный JWT токен: {}", e.getMessage());
            return false;
        }
    }

    // Получение имени пользователя из токена
    public String getEmailFromToken(String token, SecretKey secretKey) {
        return Jwts.parser()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    // Получение ролей из токена
    public List<String> getRolesFromToken(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(accessKey)
                .build()
                .parseClaimsJws(token)
                .getBody();

        Object rolesObject = claims.get("roles");
        if (rolesObject instanceof List<?>) {
            return ((List<?>) rolesObject).stream()
                    .map(Object::toString)
                    .collect(Collectors.toList());
        }
        return List.of();
    }
}