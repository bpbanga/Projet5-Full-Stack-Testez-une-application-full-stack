package com.openclassrooms.starterjwt.services;

import com.openclassrooms.starterjwt.dto.RegisterRequestDto;
import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.repositorys.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private RegisterRequestDto registerRequest;
    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        registerRequest = new RegisterRequestDto();
        registerRequest.setEmail("john.doe@example.com");
        registerRequest.setFirstName("John");
        registerRequest.setLastName("Doe");
        registerRequest.setPassword("password123");

        user = User.builder()
                .id(1L)
                .email("john.doe@example.com")
                .firstName("John")
                .lastName("Doe")
                .password("encodedPassword")
                .admin(false)
                .build();
    }

    // ---------- createUser ----------
    @Test
    void testCreateUser_ShouldCreate_WhenEmailNotExists() {
        when(userRepository.existsByEmail("john.doe@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");

        userService.createUser(registerRequest);

        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testCreateUser_ShouldThrowException_WhenEmailAlreadyExists() {
        when(userRepository.existsByEmail("john.doe@example.com")).thenReturn(true);

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> userService.createUser(registerRequest)
        );

        assertEquals("Error: Email is already taken!", exception.getMessage());
        verify(userRepository, never()).save(any());
    }

    // ---------- existsByEmail ----------
    @Test
    void testExistsByEmail_ShouldReturnTrue_WhenUserExists() {
        when(userRepository.existsByEmail("john.doe@example.com")).thenReturn(true);

        boolean exists = userService.existsByEmail("john.doe@example.com");

        assertTrue(exists);
        verify(userRepository, times(1)).existsByEmail("john.doe@example.com");
    }

    @Test
    void testExistsByEmail_ShouldReturnFalse_WhenUserNotExists() {
        when(userRepository.existsByEmail("john.doe@example.com")).thenReturn(false);

        boolean exists = userService.existsByEmail("john.doe@example.com");

        assertFalse(exists);
        verify(userRepository, times(1)).existsByEmail("john.doe@example.com");
    }

    // ---------- deleteUser ----------
    @Test
    void testDeleteUser_ShouldCallRepositoryDelete() {
        userService.deleteUser(1L);
        verify(userRepository, times(1)).deleteById(1L);
    }

    // ---------- findById ----------
    @Test
    void testFindById_ShouldReturnUser_WhenExists() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        Optional<User> result = userService.findById(1L);

        assertTrue(result.isPresent());
        assertEquals("John", result.get().getFirstName());
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void testFindById_ShouldReturnEmpty_WhenNotExists() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<User> result = userService.findById(99L);

        assertTrue(result.isEmpty());
        verify(userRepository, times(1)).findById(99L);
    }

    // ---------- findByEmail ----------
    @Test
    void testFindByEmail_ShouldReturnUser_WhenExists() {
        when(userRepository.findByEmail("john.doe@example.com")).thenReturn(Optional.of(user));

        Optional<User> result = userService.findByEmail("john.doe@example.com");

        assertTrue(result.isPresent());
        assertEquals("Doe", result.get().getLastName());
        verify(userRepository, times(1)).findByEmail("john.doe@example.com");
    }

    @Test
    void testFindByEmail_ShouldReturnEmpty_WhenNotExists() {
        when(userRepository.findByEmail("unknown@example.com")).thenReturn(Optional.empty());

        Optional<User> result = userService.findByEmail("unknown@example.com");

        assertTrue(result.isEmpty());
        verify(userRepository, times(1)).findByEmail("unknown@example.com");
    }
}
