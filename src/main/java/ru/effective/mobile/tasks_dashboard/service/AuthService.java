package ru.effective.mobile.tasks_dashboard.service;

import jakarta.validation.constraints.Email;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.effective.mobile.tasks_dashboard.exception.EmailAlreadyInUseException;
import ru.effective.mobile.tasks_dashboard.exception.IllegalCredentialsException;
import ru.effective.mobile.tasks_dashboard.model.Role;
import ru.effective.mobile.tasks_dashboard.model.User;
import ru.effective.mobile.tasks_dashboard.repository.UserRepository;

import java.util.*;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final TokenService tokenService;
    private final PasswordEncoder passwordEncoder;
    private final UserService userService;

    @Autowired
    public AuthService(UserRepository userRepository, TokenService tokenService, PasswordEncoder passwordEncoder, UserService userService) {
        this.userRepository = userRepository;
        this.tokenService = tokenService;
        this.passwordEncoder = passwordEncoder;
        this.userService = userService;
    }


    //todo переделать на email
    public void register(String email, String password) {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new EmailAlreadyInUseException("Email already in use.");
        }

        User user = new User();
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setDefaultRole();
        userRepository.save(user);
    }

    public Map<String, Object> registerWithTokens(String email, String password) {
        // Регистрация пользователя
        register(email, password);

        // Генерация пары токенов
        Map<String, String> tokenPair = tokenService.generateTokenPair(email, Collections.singleton(Role.ROLE_USER));

        // Формируем ответ
        Map<String, Object> response = new HashMap<>();
        response.put("message", "User registered successfully");
        response.put("tokens", tokenPair);

        return response;
    }

    public User authenticate(String email, String password) {
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isEmpty() || !passwordEncoder.matches(password, userOptional.get().getPassword())) {
            throw new IllegalCredentialsException("Invalid email or password");
        }
        return userOptional.get();
    }
}
