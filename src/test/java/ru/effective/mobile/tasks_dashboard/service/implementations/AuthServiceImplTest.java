package ru.effective.mobile.tasks_dashboard.service.implementations;

import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import ru.effective.mobile.tasks_dashboard.dto.auth.*;
import ru.effective.mobile.tasks_dashboard.model.User;
import ru.effective.mobile.tasks_dashboard.security.UserDetailsImpl;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthServiceImplTest {

    @Mock
    private TokenServiceImpl tokenServiceImpl;

    @Mock
    private UserServiceImpl userServiceImpl;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthServiceImpl authService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testRegisterWithTokens() {
        RegisterRequest request = new RegisterRequest("andrey@ya.ru", "password");
        User user = new User("andrey@ya.ru", "password");
        Map<String, String> tokenPair = new HashMap<>();
        tokenPair.put("access_token", "accessToken");
        tokenPair.put("refresh_token", "refreshToken");

        when(userServiceImpl.createUser(any(User.class))).thenReturn(user);
        when(tokenServiceImpl.generateTokenPair(user.getEmail(), user.getRoles())).thenReturn(tokenPair);

        RegisterResponse response = authService.registerWithTokens(request);

        assertNotNull(response);
        assertEquals("accessToken", response.getAccessToken());
        assertEquals("refreshToken", response.getRefreshToken());

        verify(userServiceImpl).createUser(any(User.class));
        verify(tokenServiceImpl).generateTokenPair(user.getEmail(), user.getRoles());
    }

    @Test
    void testLoginUser() {

        LoginRequest request = new LoginRequest("andrey@ya.ru", "password");
        User user = new User("andrey@ya.ru", "password");
        Map<String, String> tokenPair = new HashMap<>();
        tokenPair.put("access_token", "accessToken");
        tokenPair.put("refresh_token", "refreshToken");

        Authentication authentication = mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(new UserDetailsImpl(user));
        when(tokenServiceImpl.generateTokenPair(user.getEmail(), user.getRoles())).thenReturn(tokenPair);


        AuthResponse response = authService.loginUser(request);


        assertNotNull(response);
        assertEquals("accessToken", response.getAccessToken());
        assertEquals("refreshToken", response.getRefreshToken());

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(tokenServiceImpl).generateTokenPair(user.getEmail(), user.getRoles());
    }

    @Test
    void testRefreshAccessToken() {
        String refreshToken = "refreshToken";
        Map<String, String> tokenPair = new HashMap<>();
        tokenPair.put("access_token", "newAccessToken");
        tokenPair.put("refresh_token", "newRefreshToken");

        when(tokenServiceImpl.refreshAccessToken(refreshToken)).thenReturn(tokenPair);

        RefreshResponse response = authService.refreshAccessToken(refreshToken);


        assertNotNull(response);
        assertEquals("newAccessToken", response.getAccessToken());
        assertEquals("newRefreshToken", response.getRefreshToken());

        verify(tokenServiceImpl).refreshAccessToken(refreshToken);
    }

    @Test
    void testAuthenticate() {
        String email = "andrey@ya.ru";
        String password = "password";
        User user = new User(email, password);

        Authentication authentication = mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(new UserDetailsImpl(user));

        User authenticatedUser = authService.authenticate(email, password);

        assertNotNull(authenticatedUser);
        assertEquals(user.getEmail(), authenticatedUser.getEmail());
        assertEquals(user.getPassword(), authenticatedUser.getPassword());

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        assertEquals(authentication, SecurityContextHolder.getContext().getAuthentication());
    }
}