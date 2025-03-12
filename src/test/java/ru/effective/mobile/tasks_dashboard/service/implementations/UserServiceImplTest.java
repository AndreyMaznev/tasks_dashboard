package ru.effective.mobile.tasks_dashboard.service.implementations;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import org.springframework.security.crypto.password.PasswordEncoder;
import ru.effective.mobile.tasks_dashboard.exception.EmailAlreadyInUseException;
import ru.effective.mobile.tasks_dashboard.exception.IllegalJwtTokenException;
import ru.effective.mobile.tasks_dashboard.exception.UserAlreadyHaveAssignedRoleException;
import ru.effective.mobile.tasks_dashboard.exception.UserNotFoundException;
import ru.effective.mobile.tasks_dashboard.model.Role;
import ru.effective.mobile.tasks_dashboard.model.User;
import ru.effective.mobile.tasks_dashboard.repository.UserRepository;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetUserById() {
        long userId = 1L;
        User user = new User();
        user.setId(userId);
        user.setEmail("andrey@ya.ru");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        User result = userService.getUserById(userId);

        assertNotNull(result);
        assertEquals(userId, result.getId());
        assertEquals("andrey@ya.ru", result.getEmail());

        verify(userRepository).findById(userId);
    }

    @Test
    void testGetUserById_UserNotFound() {
        long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class,() -> userService.getUserById(userId));
        verify(userRepository).findById(userId);
    }

    @Test
    void testCreateUser() {
        User user = new User();
        user.setEmail("andrey@ya.ru");
        user.setPassword("password");

        String encodedPassword = "encodedPassword";

        when(userRepository.existsByEmail(user.getEmail())).thenReturn(false);
        when(passwordEncoder.encode("password")).thenReturn(encodedPassword);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User result = userService.createUser(user);

        assertNotNull(result);
        assertEquals("andrey@ya.ru", result.getEmail());
        assertEquals(encodedPassword, result.getPassword());
        assertTrue(result.getRoles().contains(Role.ROLE_USER));


        verify(userRepository).existsByEmail(user.getEmail());
        verify(passwordEncoder).encode("password");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void testCreateUser_EmailAlreadyInUse() {
        User user = new User();
        user.setEmail("andrey@ya.ru");

        when(userRepository.existsByEmail(user.getEmail())).thenReturn(true);
        assertThrows(EmailAlreadyInUseException.class, () -> userService.createUser(user));


        verify(userRepository).existsByEmail(user.getEmail());
    }

    @Test
    void testGetUserByEmail() {
        String email = "andrey@ya.ru";
        User user = new User();
        user.setEmail(email);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        User result = userService.getUserByEmail(email);

        assertNotNull(result);
        assertEquals(email, result.getEmail());

        verify(userRepository).findByEmail(email);
    }

    @Test
    void testGetUserByEmail_UserNotFound() {
        String email = "andrey@ya.ru";

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> userService.getUserByEmail(email));


        verify(userRepository).findByEmail(email);
    }

    @Test
    void testDeleteUserById() {
        long userId = 1L;

        when(userRepository.existsById(userId)).thenReturn(true);

        userService.deleteUserById(userId);

        verify(userRepository).existsById(userId);
        verify(userRepository).deleteById(userId);
    }

    @Test
    void testDeleteUserById_UserNotFound() {
        long userId = 1L;

        when(userRepository.existsById(userId)).thenReturn(false);
        assertThrows(UserNotFoundException.class, () -> userService.deleteUserById(userId));

        verify(userRepository).existsById(userId);
    }

    @Test
    void testAddRoleToUser_UserAlreadyHasRole() {

        long userId = 1L;
        Role role = Role.ROLE_USER;

        User user = new User();
        user.setId(userId);
        user.setRoles(Set.of(Role.ROLE_USER));

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        assertThrows(UserAlreadyHaveAssignedRoleException.class,
                () -> userService.addRoleToUser(userId, role));

        verify(userRepository).findById(userId);
    }

    @Test
    void testRemoveRoleFromUser_DefaultRole() {
        long userId = 1L;
        Role role = Role.ROLE_USER;

        User user = new User();
        user.setId(userId);
        user.setRoles(Set.of(Role.ROLE_USER));

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        assertThrows(IllegalArgumentException.class, () -> userService.removeRoleFromUser(userId, role));

        verify(userRepository).findById(userId);
    }

    @Test
    void testGetConvertedRoles() {
        Set<Role> roles = Set.of(Role.ROLE_USER, Role.ROLE_ADMIN);

        Set<String> result = userService.getConvertedRoles(roles);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.contains("ROLE_USER"));
        assertTrue(result.contains("ROLE_ADMIN"));
    }

    @Test
    void testGetConvertedRoles_NullRoles() {
        Set<Role> roles = null;
        Set<String> result = userService.getConvertedRoles(roles);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}