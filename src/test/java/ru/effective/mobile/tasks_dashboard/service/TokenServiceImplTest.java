package ru.effective.mobile.tasks_dashboard.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import ru.effective.mobile.tasks_dashboard.exception.IllegalJwtTokenException;
import ru.effective.mobile.tasks_dashboard.exception.UserNotFoundException;
import ru.effective.mobile.tasks_dashboard.security.JwtConfig;
import ru.effective.mobile.tasks_dashboard.service.implementations.TokenRedisServiceImpl;
import ru.effective.mobile.tasks_dashboard.service.implementations.TokenServiceImpl;
import ru.effective.mobile.tasks_dashboard.service.implementations.UserServiceImpl;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TokenServiceImplTest {

    @Mock
    private JwtConfig jwtConfig;

    @Mock
    private TokenRedisServiceImpl tokenRedisServiceImpl;

    @Mock
    private UserServiceImpl userServiceImpl;

    @InjectMocks
    private TokenServiceImpl tokenService;

    private static final String EMAIL = "test@example.com";
    private static final String ACCESS_TOKEN = "accessToken";
    private static final String REFRESH_TOKEN = "refreshToken";
    private static final Set<String> ROLES = Collections.singleton("ROLE_USER");

    @Test
    void generateTokenPair_shouldReturnTokenMap() {
        // Arrange
        when(jwtConfig.generateAccessToken(anyString(), anySet())).thenReturn(ACCESS_TOKEN);
        when(jwtConfig.generateRefreshToken(anyString())).thenReturn(REFRESH_TOKEN);
        when(userServiceImpl.getConvertedRoles(anySet())).thenReturn(ROLES);
        when(jwtConfig.getRefreshExpirationInMs()).thenReturn(3600000L); // Заглушка для getRefreshExpirationInMs

        // Act
        Map<String, String> tokenMap = tokenService.generateTokenPair(EMAIL, Collections.emptySet());

        // Assert
        assertNotNull(tokenMap);
        assertEquals(ACCESS_TOKEN, tokenMap.get("access_token"));
        assertEquals(REFRESH_TOKEN, tokenMap.get("refresh_token"));

        // Verify interactions
        verify(jwtConfig).generateAccessToken(EMAIL, ROLES);
        verify(jwtConfig).generateRefreshToken(EMAIL);
        verify(jwtConfig).getRefreshExpirationInMs(); // Проверка вызова getRefreshExpirationInMs
        verify(tokenRedisServiceImpl).saveRefreshToken(REFRESH_TOKEN, EMAIL, 3600000L);
        verify(userServiceImpl).getConvertedRoles(Collections.emptySet());
        verifyNoMoreInteractions(jwtConfig, tokenRedisServiceImpl, userServiceImpl);
    }

    @Test
    void refreshAccessToken_shouldThrowException_whenTokenIsInvalid() {
        // Arrange
        when(tokenRedisServiceImpl.isRefreshTokenValid(REFRESH_TOKEN)).thenReturn(false);

        // Act & Assert
        IllegalJwtTokenException exception = assertThrows(IllegalJwtTokenException.class,
                () -> tokenService.refreshAccessToken(REFRESH_TOKEN));

        assertEquals("Некорректный или просроченный токен.", exception.getMessage());

        // Verify interactions
        verify(tokenRedisServiceImpl).isRefreshTokenValid(REFRESH_TOKEN);
        verifyNoMoreInteractions(tokenRedisServiceImpl, jwtConfig, userServiceImpl);
    }

    @Test
    void refreshAccessToken_shouldThrowException_whenTokenIsUsed() {
        // Arrange
        when(tokenRedisServiceImpl.isRefreshTokenValid(REFRESH_TOKEN)).thenReturn(true);
        when(tokenRedisServiceImpl.isTokenUsed(REFRESH_TOKEN)).thenReturn(true);

        // Act & Assert
        IllegalJwtTokenException exception = assertThrows(IllegalJwtTokenException.class,
                () -> tokenService.refreshAccessToken(REFRESH_TOKEN));

        assertEquals("Токен уже использовался.", exception.getMessage());

        // Verify interactions
        verify(tokenRedisServiceImpl).isRefreshTokenValid(REFRESH_TOKEN);
        verify(tokenRedisServiceImpl).isTokenUsed(REFRESH_TOKEN);
        verifyNoMoreInteractions(tokenRedisServiceImpl, jwtConfig, userServiceImpl);
    }

    @Test
    void refreshAccessToken_shouldReturnNewTokenPair_whenTokenIsValid() throws UserNotFoundException {
        // Arrange
        when(tokenRedisServiceImpl.isRefreshTokenValid(REFRESH_TOKEN)).thenReturn(true);
        when(tokenRedisServiceImpl.isTokenUsed(REFRESH_TOKEN)).thenReturn(false);
        when(tokenRedisServiceImpl.getUserEmailFromRefreshToken(REFRESH_TOKEN)).thenReturn(EMAIL);
        when(userServiceImpl.getUserRolesByEmail(EMAIL)).thenReturn(Collections.emptySet());
        when(userServiceImpl.getConvertedRoles(anySet())).thenReturn(ROLES);
        when(jwtConfig.generateAccessToken(anyString(), anySet())).thenReturn(ACCESS_TOKEN);
        when(jwtConfig.generateRefreshToken(anyString())).thenReturn(REFRESH_TOKEN);
        when(jwtConfig.getRefreshExpirationInMs()).thenReturn(3600000L); // Заглушка для getRefreshExpirationInMs

        // Act
        Map<String, String> tokenMap = tokenService.refreshAccessToken(REFRESH_TOKEN);

        // Assert
        assertNotNull(tokenMap);
        assertEquals(ACCESS_TOKEN, tokenMap.get("access_token"));
        assertEquals(REFRESH_TOKEN, tokenMap.get("refresh_token"));

        // Verify interactions
        verify(tokenRedisServiceImpl).isRefreshTokenValid(REFRESH_TOKEN);
        verify(tokenRedisServiceImpl).isTokenUsed(REFRESH_TOKEN);
        verify(tokenRedisServiceImpl).getUserEmailFromRefreshToken(REFRESH_TOKEN);
        verify(userServiceImpl).getUserRolesByEmail(EMAIL);
        verify(userServiceImpl).getConvertedRoles(Collections.emptySet());
        verify(jwtConfig).generateAccessToken(EMAIL, ROLES);
        verify(jwtConfig).generateRefreshToken(EMAIL);
        verify(jwtConfig).getRefreshExpirationInMs(); // Проверка вызова getRefreshExpirationInMs
        verify(tokenRedisServiceImpl).saveRefreshToken(REFRESH_TOKEN, EMAIL, 3600000L);
        verify(tokenRedisServiceImpl).markTokenAsUsed(REFRESH_TOKEN);
        verify(tokenRedisServiceImpl).revokeRefreshToken(REFRESH_TOKEN);
        verifyNoMoreInteractions(jwtConfig, tokenRedisServiceImpl, userServiceImpl);
    }
}