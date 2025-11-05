package com.openclassrooms.starterjwt.controllers;

import com.openclassrooms.starterjwt.dto.LoginRequestDto;
import com.openclassrooms.starterjwt.dto.RegisterRequestDto;
import com.openclassrooms.starterjwt.dto.TokenResponseDto;
import com.openclassrooms.starterjwt.services.AuthenticationService;
import com.openclassrooms.starterjwt.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AuthentificationControllerTest  {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationService authenticationService;

    @Mock
    private UserService userService;

    @InjectMocks
    private AuthentificationController authController;

    private LoginRequestDto loginRequest;
    private RegisterRequestDto registerRequest;
    private TokenResponseDto tokenResponse;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        loginRequest = new LoginRequestDto("user@test.com", "password");
        registerRequest = new RegisterRequestDto("user@test.com", "password", "John", "Doe");
        tokenResponse = new TokenResponseDto("token", null, null, null, null, true);
    }

    @Test
    void testLogin_Success() {
        Authentication auth = mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(auth);
        when(authenticationService.generateToken(auth)).thenReturn(tokenResponse);

        ResponseEntity<TokenResponseDto> response = authController.login(loginRequest);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(tokenResponse);
    }

    @Test
    void testLogin_Failure() {
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new RuntimeException("Bad credentials"));

        ResponseEntity<TokenResponseDto> response = authController.login(loginRequest);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(response.getBody().getToken()).isEqualTo("Invalid email or password");
    }

    @Test
    void testRegister_EmailTaken() {
        when(userService.existsByEmail(registerRequest.getEmail())).thenReturn(true);

        ResponseEntity<TokenResponseDto> response = authController.register(registerRequest);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody().getToken()).isEqualTo("Error: Email is already taken!"); 
    }

    @Test
    void testRegister_Success() {
        when(userService.existsByEmail(registerRequest.getEmail())).thenReturn(false);
        when(authenticationService.authenticate(any(LoginRequestDto.class))).thenReturn(tokenResponse);

        ResponseEntity<TokenResponseDto> response = authController.register(registerRequest);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(tokenResponse);
        verify(userService, times(1)).createUser(registerRequest);
    }
}
