package com.openclassrooms.starterjwt.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.IOException;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.assertThat;

class JwtAuthenticationFilterTest {

    @Mock
    private JwtService jwtService;

    @Mock
    private UserDetailsServiceImplementation userDetailsService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        jwtAuthenticationFilter = new JwtAuthenticationFilter(jwtService, userDetailsService);
        SecurityContextHolder.clearContext();
    }

    /**
     *  Cas 1 : Token totalement invalide (parseJwt() renvoie un token, mais extractUsername() -> null)
     */
    @Test
    void shouldContinueFilter_WhenTokenCorrupted() throws Exception {
        when(request.getRequestURI()).thenReturn("/api/sessions");
        when(request.getHeader("Authorization")).thenReturn("Bearer invalidtoken");
        when(jwtService.extractUsername("invalidtoken")).thenReturn(null);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verify(jwtService).extractUsername("invalidtoken");
        verify(jwtService, never()).validateToken(any(), any());
        verify(filterChain).doFilter(request, response);
    }

    /**
     *  Cas 2 : Token contient un username, mais invalide lors de la validation (mauvaise signature / expiré)
     */
    @Test
    void shouldReturnUnauthorized_WhenTokenInvalid() throws Exception {
        when(request.getRequestURI()).thenReturn("/api/sessions");
        when(request.getHeader("Authorization")).thenReturn("Bearer invalidtoken");
        when(jwtService.extractUsername("invalidtoken")).thenReturn("john@example.com");

        UserDetails userDetails = new User("john@example.com", "pwd", List.of());
        when(userDetailsService.loadUserByUsername("john@example.com")).thenReturn(userDetails);
        when(jwtService.validateToken("invalidtoken", userDetails)).thenReturn(false);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verify(jwtService).extractUsername("invalidtoken");
        verify(jwtService).validateToken("invalidtoken", userDetails);
        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        verify(filterChain, never()).doFilter(request, response);
    }

    /**
     *  Cas 3 : Token valide, l'utilisateur est authentifié et la requête continue
     */
    @Test
    void shouldAuthenticateUser_WhenTokenValid() throws Exception {
        when(request.getRequestURI()).thenReturn("/api/sessions");
        when(request.getHeader("Authorization")).thenReturn("Bearer validtoken");
        when(jwtService.extractUsername("validtoken")).thenReturn("john@example.com");

        UserDetails userDetails = new User("john@example.com", "pwd", List.of());
        when(userDetailsService.loadUserByUsername("john@example.com")).thenReturn(userDetails);
        when(jwtService.validateToken("validtoken", userDetails)).thenReturn(true);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verify(jwtService).extractUsername("validtoken");
        verify(jwtService).validateToken("validtoken", userDetails);
        verify(filterChain).doFilter(request, response);

        var authentication = SecurityContextHolder.getContext().getAuthentication();
        assertThat(authentication)
                .isInstanceOf(UsernamePasswordAuthenticationToken.class)
                .extracting("principal")
                .isEqualTo(userDetails);
    }

    /**
     *  Cas 4 : URI exclue (/auth/login) → aucun filtrage
     */
    @Test
    void shouldNotFilterExcludedPaths() throws Exception {
        when(request.getRequestURI()).thenReturn("/auth/login");

        boolean shouldNotFilter = jwtAuthenticationFilter.shouldNotFilter(request);
        assertThat(shouldNotFilter).isTrue();
    }
}
