package com.openclassrooms.starterjwt.controllers;

import com.openclassrooms.starterjwt.dto.LoginRequestDto;
import com.openclassrooms.starterjwt.dto.RegisterRequestDto;
import com.openclassrooms.starterjwt.dto.TokenResponseDto;
import com.openclassrooms.starterjwt.services.AuthenticationService;
import com.openclassrooms.starterjwt.services.UserService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

/**
 * Contrôleur d'authentification.
 */
@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*", maxAge = 3600)
@Slf4j
public class AuthentificationController {

    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationService authenticationService;
    private final UserService userService;

    public AuthentificationController(AuthenticationManager authenticationManager,
                                      PasswordEncoder passwordEncoder,
                                      AuthenticationService authenticationService,
                                      UserService userService) {
        this.authenticationManager = authenticationManager;
        this.passwordEncoder = passwordEncoder;
        this.authenticationService = authenticationService;
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponseDto> login(@Valid @RequestBody LoginRequestDto loginRequest) {
        log.debug("Authenticating user: {}", loginRequest.getEmail());
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            TokenResponseDto tokenResponse = authenticationService.generateToken(authentication);

            log.debug("User {} successfully authenticated.", loginRequest.getEmail());
            return ResponseEntity.ok(tokenResponse);

        } catch (Exception e) {
            log.error("Authentication failed for {}: {}", loginRequest.getEmail(), e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new TokenResponseDto("Invalid email or password", null, null, null, null, false));
        }
    }

    @PostMapping("/register")
    public ResponseEntity<TokenResponseDto> register(@Valid @RequestBody RegisterRequestDto registerRequest) {
        log.debug("Registering new user: {}", registerRequest.getEmail());

        if (userService.existsByEmail(registerRequest.getEmail())) {
            log.warn("Email {} is already taken.", registerRequest.getEmail());
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new TokenResponseDto("Error: Email is already taken!", null, null, null, null, false));
        }

        userService.createUser(registerRequest);
        log.debug("User {} created successfully.", registerRequest.getEmail());

        LoginRequestDto loginRequest = new LoginRequestDto(registerRequest.getEmail(), registerRequest.getPassword());
        TokenResponseDto tokenResponse = authenticationService.authenticate(loginRequest);

        return ResponseEntity.ok(tokenResponse);
    }
}
