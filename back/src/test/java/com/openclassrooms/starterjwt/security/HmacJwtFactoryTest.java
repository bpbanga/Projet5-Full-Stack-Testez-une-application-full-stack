package com.openclassrooms.starterjwt.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import static org.assertj.core.api.Assertions.*;

class HmacJwtFactoryTest {

    private HmacJwtFactory jwtFactory;

    @BeforeEach
    void setUp() {
        jwtFactory = new HmacJwtFactory("12345678901234567890123456789012");
    }

    @Test
    void generateToken_ShouldProduceValidToken() {
        Authentication auth = new UsernamePasswordAuthenticationToken("john@example.com", "password");
        String token = jwtFactory.generateToken(auth);

        assertThat(token).isNotNull();
        assertThat(jwtFactory.extractUsername(token)).isEqualTo("john@example.com");
    }

    @Test
    void validateToken_ShouldReturnTrue_WhenUsernameMatches() {
        Authentication auth = new UsernamePasswordAuthenticationToken("john@example.com", "password");
        String token = jwtFactory.generateToken(auth);

        assertThat(jwtFactory.validateToken(token, "john@example.com")).isTrue();
    }

    @Test
    void validateToken_ShouldReturnFalse_WhenUsernameDoesNotMatch() {
        Authentication auth = new UsernamePasswordAuthenticationToken("john@example.com", "password");
        String token = jwtFactory.generateToken(auth);

        assertThat(jwtFactory.validateToken(token, "different@example.com")).isFalse();
    }

    @Test
    void constructor_ShouldThrow_WhenSecretTooShort() {
        assertThatThrownBy(() -> new HmacJwtFactory("short"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("at least 32 characters");
    }
}
