package ru.effective.mobile.tasks_dashboard.service.implementations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import ru.effective.mobile.tasks_dashboard.dto.auth.*;
import ru.effective.mobile.tasks_dashboard.model.User;
import ru.effective.mobile.tasks_dashboard.security.UserDetailsImpl;
import ru.effective.mobile.tasks_dashboard.service.interfaces.AuthService;

import java.util.*;

@Service
public class AuthServiceImpl implements AuthService {

    private final TokenServiceImpl tokenServiceImpl;
    private final UserServiceImpl userServiceImpl;
    private final AuthenticationManager authenticationManager;

    @Autowired
    public AuthServiceImpl(TokenServiceImpl tokenServiceImpl, UserServiceImpl userServiceImpl, AuthenticationManager authenticationManager) {
        this.tokenServiceImpl = tokenServiceImpl;
        this.userServiceImpl = userServiceImpl;
        this.authenticationManager = authenticationManager;
    }

    public RegisterResponse registerWithTokens(RegisterRequest request) {
        User user = userServiceImpl.createUser(new User(request.getEmail(), request.getPassword()));
        Map <String, String > tokenPair = tokenServiceImpl.generateTokenPair(user.getEmail(), user.getRoles());
        return RegisterResponse.builder()
                .message("User registered successfully")
                .accessToken(tokenPair.get("access_token"))
                .refreshToken(tokenPair.get("refresh_token"))
                .build();
    }

    public AuthResponse loginUser(LoginRequest request) {
        User user = authenticate(request.getEmail(), request.getPassword());
        Map <String, String > tokenPair = tokenServiceImpl.generateTokenPair(user.getEmail(), user.getRoles());
        return AuthResponse.builder()
                .message("Login success")
                .accessToken(tokenPair.get("access_token"))
                .refreshToken(tokenPair.get("refresh_token"))
                .build();
    }

    public RefreshResponse refreshAccessToken(String refreshToken) {
        Map<String, String> tokenPair = tokenServiceImpl.refreshAccessToken(refreshToken);
        return RefreshResponse.builder()
                .message("Refresh success")
                .accessToken(tokenPair.get("access_token"))
                .refreshToken(tokenPair.get("refresh_token"))
                .build();
    }

    public User authenticate(String email, String password) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, password)
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        return ((UserDetailsImpl) authentication.getPrincipal()).getUser();
    }
}
