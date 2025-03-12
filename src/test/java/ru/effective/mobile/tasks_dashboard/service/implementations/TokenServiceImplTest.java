package ru.effective.mobile.tasks_dashboard.service.implementations;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.effective.mobile.tasks_dashboard.exception.IllegalJwtTokenException;
import ru.effective.mobile.tasks_dashboard.exception.UserNotFoundException;
import ru.effective.mobile.tasks_dashboard.security.JwtConfig;

import java.util.Collections;
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

    private static final String EMAIL = "andrey@ya.ru";
    private static final String ACCESS_TOKEN = "accessToken";
    private static final String REFRESH_TOKEN = "refreshToken";
    private static final Set<String> ROLES = Collections.singleton("ROLE_USER");

    @Test
    void generateTokenPair_shouldReturnTokenMap() {
        when(jwtConfig.generateAccessToken(anyString(), anySet())).thenReturn(ACCESS_TOKEN);
        when(jwtConfig.generateRefreshToken(anyString())).thenReturn(REFRESH_TOKEN);
        when(userServiceImpl.getConvertedRoles(anySet())).thenReturn(ROLES);
        when(jwtConfig.getRefreshExpirationInMs()).thenReturn(3600000L);

        Map<String, String> tokenMap = tokenService.generateTokenPair(EMAIL, Collections.emptySet());

        assertNotNull(tokenMap);
        assertEquals(ACCESS_TOKEN, tokenMap.get("access_token"));
        assertEquals(REFRESH_TOKEN, tokenMap.get("refresh_token"));

        verify(jwtConfig).generateAccessToken(EMAIL, ROLES);
        verify(jwtConfig).generateRefreshToken(EMAIL);
        verify(jwtConfig).getRefreshExpirationInMs();
        verify(tokenRedisServiceImpl).saveRefreshToken(REFRESH_TOKEN, EMAIL, 3600000L);
        verify(userServiceImpl).getConvertedRoles(Collections.emptySet());
        verifyNoMoreInteractions(jwtConfig, tokenRedisServiceImpl, userServiceImpl);
    }

    @Test
    void refreshAccessToken_shouldThrowException_whenTokenIsInvalid() {
        when(tokenRedisServiceImpl.isRefreshTokenValid(REFRESH_TOKEN)).thenReturn(false);
        assertThrows(IllegalJwtTokenException.class,
                () -> tokenService.refreshAccessToken(REFRESH_TOKEN));
        verify(tokenRedisServiceImpl).isRefreshTokenValid(REFRESH_TOKEN);
        verifyNoMoreInteractions(tokenRedisServiceImpl, jwtConfig, userServiceImpl);
    }

    @Test
    void refreshAccessToken_shouldThrowException_whenTokenIsUsed() {
        when(tokenRedisServiceImpl.isRefreshTokenValid(REFRESH_TOKEN)).thenReturn(true);
        when(tokenRedisServiceImpl.isTokenUsed(REFRESH_TOKEN)).thenReturn(true);

        assertThrows(IllegalJwtTokenException.class,
                () -> tokenService.refreshAccessToken(REFRESH_TOKEN));

        verify(tokenRedisServiceImpl).isRefreshTokenValid(REFRESH_TOKEN);
        verify(tokenRedisServiceImpl).isTokenUsed(REFRESH_TOKEN);
        verifyNoMoreInteractions(tokenRedisServiceImpl, jwtConfig, userServiceImpl);
    }

    @Test
    void refreshAccessToken_shouldReturnNewTokenPair_whenTokenIsValid() throws UserNotFoundException {

        when(tokenRedisServiceImpl.isRefreshTokenValid(REFRESH_TOKEN)).thenReturn(true);
        when(tokenRedisServiceImpl.isTokenUsed(REFRESH_TOKEN)).thenReturn(false);
        when(tokenRedisServiceImpl.getUserEmailFromRefreshToken(REFRESH_TOKEN)).thenReturn(EMAIL);
        when(userServiceImpl.getUserRolesByEmail(EMAIL)).thenReturn(Collections.emptySet());
        when(userServiceImpl.getConvertedRoles(anySet())).thenReturn(ROLES);
        when(jwtConfig.generateAccessToken(anyString(), anySet())).thenReturn(ACCESS_TOKEN);
        when(jwtConfig.generateRefreshToken(anyString())).thenReturn(REFRESH_TOKEN);
        when(jwtConfig.getRefreshExpirationInMs()).thenReturn(3600000L);

        Map<String, String> tokenMap = tokenService.refreshAccessToken(REFRESH_TOKEN);

        assertNotNull(tokenMap);
        assertEquals(ACCESS_TOKEN, tokenMap.get("access_token"));
        assertEquals(REFRESH_TOKEN, tokenMap.get("refresh_token"));

        verify(tokenRedisServiceImpl).isRefreshTokenValid(REFRESH_TOKEN);
        verify(tokenRedisServiceImpl).isTokenUsed(REFRESH_TOKEN);
        verify(tokenRedisServiceImpl).getUserEmailFromRefreshToken(REFRESH_TOKEN);
        verify(userServiceImpl).getUserRolesByEmail(EMAIL);
        verify(userServiceImpl).getConvertedRoles(Collections.emptySet());
        verify(jwtConfig).generateAccessToken(EMAIL, ROLES);
        verify(jwtConfig).generateRefreshToken(EMAIL);
        verify(jwtConfig).getRefreshExpirationInMs();
        verify(tokenRedisServiceImpl).saveRefreshToken(REFRESH_TOKEN, EMAIL, 3600000L);
        verify(tokenRedisServiceImpl).markTokenAsUsed(REFRESH_TOKEN);
        verify(tokenRedisServiceImpl).revokeRefreshToken(REFRESH_TOKEN);
        verifyNoMoreInteractions(jwtConfig, tokenRedisServiceImpl, userServiceImpl);
    }
}