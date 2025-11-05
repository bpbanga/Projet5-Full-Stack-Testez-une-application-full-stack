package com.openclassrooms.starterjwt.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class JwtServiceTest {

    @Mock
    private HmacJwtFactory jwtFactory;

    @Mock
    private Authentication authentication;

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        jwtService = new JwtService(jwtFactory);
    }

    @Test
    void generateToken_ShouldDelegateToFactory() {
        when(jwtFactory.generateToken(authentication)).thenReturn("token123");

        String token = jwtService.generateToken(authentication);

        assertThat(token).isEqualTo("token123");
        verify(jwtFactory).generateToken(authentication);
    }

    @Test
    void extractUsername_ShouldDelegateToFactory() {
        when(jwtFactory.extractUsername("token123")).thenReturn("john@example.com");

        String username = jwtService.extractUsername("token123");

        assertThat(username).isEqualTo("john@example.com");
        verify(jwtFactory).extractUsername("token123");
    }

    @Test
    void validateToken_ShouldReturnTrue_WhenUsernameMatches() {
        UserDetails userDetails = new User("john@example.com", "password", java.util.Collections.emptyList());
        when(jwtFactory.extractUsername("token123")).thenReturn("john@example.com");

        boolean result = jwtService.validateToken("token123", userDetails);

        assertThat(result).isTrue();
    }

    @Test
    void validateToken_ShouldReturnFalse_WhenUsernameDiffers() {
        UserDetails userDetails = new User("different@example.com", "password", java.util.Collections.emptyList());
        when(jwtFactory.extractUsername("token123")).thenReturn("john@example.com");

        boolean result = jwtService.validateToken("token123", userDetails);

        assertThat(result).isFalse();
    }
}
