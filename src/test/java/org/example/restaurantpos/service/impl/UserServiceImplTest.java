package org.example.restaurantpos.service.impl;

import jakarta.persistence.EntityNotFoundException;
import org.example.restaurantpos.entity.Role;
import org.example.restaurantpos.entity.User;
import org.example.restaurantpos.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        Role role = new Role();
        role.setId(1L);
        role.setRoleName("ROLE_ADMIN");

        user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setPassword("plainpass");
        user.setRole(role);
        user.setLocked(false);
    }

    @Test
    void shouldGetAllUsers() {
        when(userRepository.findAll()).thenReturn(List.of(user));

        List<User> users = userService.getAllUsers();

        assertEquals(1, users.size());
        assertEquals("testuser", users.get(0).getUsername());
        verify(userRepository).findAll();
    }

    @Test
    void shouldCreateUserWithEncodedPassword() {
        when(passwordEncoder.encode("plainpass")).thenReturn("encodedpass");
        when(userRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        User created = userService.createUser(user);

        assertNotNull(created);
        assertEquals("encodedpass", created.getPassword());
        verify(passwordEncoder).encode("plainpass");
        verify(userRepository).save(any());
    }

    @Test
    void shouldGetUserById() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        User result = userService.getUserById(1L);

        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        verify(userRepository).findById(1L);
    }

    @Test
    void shouldThrowWhenUserNotFoundById() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> userService.getUserById(1L));
        verify(userRepository).findById(1L);
    }

    @Test
    void shouldUpdateUser() {
        User updated = new User();
        updated.setUsername("updateduser");
        updated.setPassword("newpass");
        updated.setRole(user.getRole());
        updated.setLocked(true);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(passwordEncoder.encode("newpass")).thenReturn("hashedpass");
        when(userRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        User result = userService.updateUser(1L, updated);

        assertEquals("updateduser", result.getUsername());
        assertEquals("hashedpass", result.getPassword());
        assertTrue(result.isLocked());

        verify(passwordEncoder).encode("newpass");
        verify(userRepository).save(any());
    }

    @Test
    void shouldDeleteUser() {
        when(userRepository.existsById(1L)).thenReturn(true);

        userService.deleteUser(1L);

        verify(userRepository).deleteById(1L);
    }

    @Test
    void shouldThrowWhenDeletingNonexistentUser() {
        when(userRepository.existsById(1L)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> userService.deleteUser(1L));
        verify(userRepository, never()).deleteById(any());
    }
}
