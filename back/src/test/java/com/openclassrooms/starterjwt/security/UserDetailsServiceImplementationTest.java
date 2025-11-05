package com.openclassrooms.starterjwt.security;

import com.openclassrooms.starterjwt.models.AppUserDetails;
import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.repositorys.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class UserDetailsServiceImplementationTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserDetailsServiceImplementation userDetailsService;

    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        user = User.builder()
                .id(1L)
                .email("test@example.com")
                .password("password")
                .firstName("John")
                .lastName("Doe")
                .admin(false)
                .build();
    }

    @Test
    void loadUserByUsername_ShouldReturnUserDetails_WhenUserExists() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));

        AppUserDetails result = (AppUserDetails) userDetailsService.loadUserByUsername("test@example.com");

        assertThat(result).isNotNull();
        assertThat(result.getUsername()).isEqualTo("test@example.com");
        verify(userRepository).findByEmail("test@example.com");
    }

    @Test
    void loadUserByUsername_ShouldThrow_WhenUserNotFound() {
        when(userRepository.findByEmail("missing@example.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userDetailsService.loadUserByUsername("missing@example.com"))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessageContaining("Utilisateur non trouvé");
    }
}
