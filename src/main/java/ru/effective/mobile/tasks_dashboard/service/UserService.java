package ru.effective.mobile.tasks_dashboard.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.effective.mobile.tasks_dashboard.exception.UserAlreadyHaveAssignedRoleException;
import ru.effective.mobile.tasks_dashboard.exception.UserNotFoundException;
import ru.effective.mobile.tasks_dashboard.model.Role;
import ru.effective.mobile.tasks_dashboard.model.User;
import ru.effective.mobile.tasks_dashboard.repository.UserRepository;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;


    public User createUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(() -> new UserNotFoundException(email));
    }

    @Transactional
    public void deleteUserById(UUID userId) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException("User not found with ID: " + userId);
        }
        userRepository.deleteById(userId);
    }

    @Transactional
    public void addRoleToUser(UUID userId, Role role) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));

        if (!user.getRoles().contains(role)) {
            user.getRoles().add(role);
            userRepository.save(user);
        } else {
            throw new UserAlreadyHaveAssignedRoleException("User already have assigned role: " + role);
        }
    }

    //Метод для конвертации ролей в String, для JWT.
    public Set <String> getConvertedRoles(Set <Role> roles) {
        if (roles == null) {
            return Set.of();
        }
        return roles.stream()
                .map(Role::name)
                .collect(Collectors.toSet());
    }

    @Transactional(readOnly = true)
    public Set<Role> getUserRolesByUUID(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));
        return user.getRoles();
    }

    @Transactional(readOnly = true)
    public Set<Role> getUserRolesByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));
        return user.getRoles();
    }

    @Transactional(readOnly = true)
    public boolean hasRole(UUID userId, Role role) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));
        return user.getRoles().contains(role);
    }

    @Transactional
    public void removeRoleFromUser(UUID userId, Role role) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));

        if (role == Role.ROLE_USER) {
            throw new IllegalArgumentException("Cannot remove the default ROLE_USER");
        }

        // Удаляем роль, если она существует у пользователя
        if (user.getRoles().contains(role)) {
            user.getRoles().remove(role);
            userRepository.save(user);
        } else {
            throw new IllegalArgumentException("Role " + role + " is not assigned to the user");
        }
    }
}
