package com.openclassrooms.starterjwt.services;

import com.openclassrooms.starterjwt.dto.LoginRequestDto;
import com.openclassrooms.starterjwt.dto.TokenResponseDto;
import com.openclassrooms.starterjwt.exceptions.AuthenticatedUserNotFound;
import com.openclassrooms.starterjwt.models.AppUserDetails;
import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.security.JwtService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class AuthenticationServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthenticationService authenticationService;

    @Mock
    private Authentication authentication;

    @Mock
    private AppUserDetails userDetails;

    @Mock
    private SecurityContext securityContext;

    private User user;

    @BeforeEach
    void setup() {
        user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setAdmin(true);
        SecurityContextHolder.setContext(securityContext);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void testAuthenticate_success() {
        LoginRequestDto request = new LoginRequestDto("test@example.com", "password123");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUser()).thenReturn(user);
        when(jwtService.generateToken(authentication)).thenReturn("token123");

        TokenResponseDto result = authenticationService.authenticate(request);

        assertEquals("token123", result.getToken());
        assertEquals(user.getId(), result.getId());
        assertEquals(user.getEmail(), result.getEmail());
        assertEquals(user.getFirstName(), result.getFirstName());
        assertEquals(user.getLastName(), result.getLastName());
        assertEquals(user.isAdmin(), result.isAdmin());
    }

    @Test
    void testAuthenticate_authenticationFails() {
        LoginRequestDto request = new LoginRequestDto("test@example.com", "wrongpassword");
        when(authenticationManager.authenticate(any())).thenThrow(new BadCredentialsException("Bad credentials"));
        assertThrows(AuthenticationServiceException.class, () -> authenticationService.authenticate(request));
    }

    @Test
    void testGenerateToken_success() {
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUser()).thenReturn(user);
        when(jwtService.generateToken(authentication)).thenReturn("token123");

        TokenResponseDto result = authenticationService.generateToken(authentication);

        assertEquals("token123", result.getToken());
        assertEquals(user.getId(), result.getId());
        assertEquals(user.getEmail(), result.getEmail());
    }

    @Test
    void testGenerateToken_userNotFound() {
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUser()).thenReturn(null);
        when(jwtService.generateToken(authentication)).thenReturn("token123");

        assertThrows(AuthenticationServiceException.class, () -> authenticationService.generateToken(authentication));
    }

    @Test
    void testGetAuthenticatedUserEmail_success() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("test@example.com");

        String email = authenticationService.getAuthenticatedUserEmail();

        assertEquals("test@example.com", email);
        verify(securityContext).getAuthentication();
    }

    @Test
    void testGetAuthenticatedUserEmail_noAuthenticatedUser() {
        when(securityContext.getAuthentication()).thenReturn(null);

        AuthenticatedUserNotFound exception = assertThrows(
                AuthenticatedUserNotFound.class,
                () -> authenticationService.getAuthenticatedUserEmail()
        );

        assertTrue(exception.getMessage().contains("No authenticated user found"));
    }
}
