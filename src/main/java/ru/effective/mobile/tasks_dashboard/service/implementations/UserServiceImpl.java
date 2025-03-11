package ru.effective.mobile.tasks_dashboard.service.implementations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.effective.mobile.tasks_dashboard.exception.EmailAlreadyInUseException;
import ru.effective.mobile.tasks_dashboard.exception.UserAlreadyHaveAssignedRoleException;
import ru.effective.mobile.tasks_dashboard.exception.UserNotFoundException;
import ru.effective.mobile.tasks_dashboard.model.Role;
import ru.effective.mobile.tasks_dashboard.model.User;
import ru.effective.mobile.tasks_dashboard.repository.UserRepository;
import ru.effective.mobile.tasks_dashboard.service.interfaces.UserService;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Cacheable(value = "users", key = "#userId")
    public User getUserById(long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("Пользователь с id : " + userId + " не найден."));
    }

    @CachePut(value = "users", key = "#result.id")
    @Transactional
    public User createUser(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new EmailAlreadyInUseException("Указанный email уже используется.");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRoles(Set.of(Role.ROLE_USER));
        return userRepository.save(user);
    }

    @Cacheable(value = "users", key = "#email")
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(() -> new UserNotFoundException(email));
    }

    @CacheEvict(value = "users", key = "#userId")
    @Transactional
    public void deleteUserById(long userId) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException("Пользователь с id : " + userId + " не найден.");
        }
        userRepository.deleteById(userId);
    }

    @CacheEvict(value = "users", key = "#userId")
    @Transactional
    public void addRoleToUser(long userId, Role role) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Пользователь с id : " + userId + " не найден."));

        if (!user.getRoles().contains(role)) {
            user.getRoles().add(role);
            userRepository.save(user);
        } else {
            throw new UserAlreadyHaveAssignedRoleException("У пользователя уже есть эта роль : " + role);
        }
    }

    @CacheEvict(value = "users", key = "#userId")
    @Transactional
    public void removeRoleFromUser(long userId, Role role) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Пользователь с id : " + userId + " не найден."));
        if (role == Role.ROLE_USER) {
            throw new IllegalArgumentException("Невозможно удалить роль по-умолчанию : ROLE_USER");
        }
        if (user.getRoles().contains(role)) {
            user.getRoles().remove(role);
            userRepository.save(user);
        } else {
            throw new IllegalArgumentException("Роли " + role + " нет у данного пользователя");
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

    @CacheEvict(value = "users", key = "#userId")
    @Transactional(readOnly = true)
    public Set<Role> getUserRolesByUserId(long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Пользователь с id : " + userId + " не найден."));
        return user.getRoles();
    }

    @Transactional(readOnly = true)
    public Set<Role> getUserRolesByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("Пользователь с email : " + email + " не найден."));
        return user.getRoles();
    }

    public boolean hasRole(long userId, Role role) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Пользователь с id : " + userId + " не найден."));
        return user.getRoles().contains(role);
    }


}
