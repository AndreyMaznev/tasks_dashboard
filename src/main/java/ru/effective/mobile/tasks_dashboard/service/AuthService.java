package ru.effective.mobile.tasks_dashboard.service;

import jakarta.validation.constraints.Email;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.effective.mobile.tasks_dashboard.dto.*;
import ru.effective.mobile.tasks_dashboard.exception.EmailAlreadyInUseException;
import ru.effective.mobile.tasks_dashboard.exception.IllegalCredentialsException;
import ru.effective.mobile.tasks_dashboard.model.Role;
import ru.effective.mobile.tasks_dashboard.model.User;
import ru.effective.mobile.tasks_dashboard.repository.UserRepository;
import ru.effective.mobile.tasks_dashboard.security.UserDetailsImpl;

import java.util.*;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final TokenService tokenService;
    private final PasswordEncoder passwordEncoder;
    private final UserService userService;
    private final AuthenticationManager authenticationManager;

    @Autowired
    public AuthService(UserRepository userRepository, TokenService tokenService, PasswordEncoder passwordEncoder, UserService userService, AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.tokenService = tokenService;
        this.passwordEncoder = passwordEncoder;
        this.userService = userService;
        this.authenticationManager = authenticationManager;
    }


    public RegisterResponse registerWithTokens(RegisterRequest request) {
        User user = userService.createUser(new User(request.getEmail(), request.getPassword()));
        Map <String, String > tokenPair = tokenService.generateTokenPair(user.getEmail(), user.getRoles());
        return RegisterResponse.builder()
                .message("User registered successfully")
                .accessToken(tokenPair.get("access_token"))
                .refreshToken(tokenPair.get("refresh_token"))
                .build();
    }

    public AuthResponse loginUser(LoginRequest request) {
        User user = authenticate(request.getEmail(), request.getPassword());
        Map <String, String > tokenPair = tokenService.generateTokenPair(user.getEmail(), user.getRoles());
        return AuthResponse.builder()
                .message("Login success")
                .accessToken(tokenPair.get("access_token"))
                .refreshToken(tokenPair.get("refresh_token"))
                .build();
    }

    public RefreshResponse refreshToken(String refreshToken) {
        Map<String, String> tokenPair = tokenService.refreshAccessToken(refreshToken);
        return RefreshResponse.builder()
                .message("Refresh success")
                .accessToken(tokenPair.get("access_token"))
                .refreshToken(tokenPair.get("refresh_token"))
                .build();
    }

    //todo убрать дублирование кода для DTOшек

    public User authenticate(String email, String password) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, password)
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        return ((UserDetailsImpl) authentication.getPrincipal()).getUser();
    }
}
