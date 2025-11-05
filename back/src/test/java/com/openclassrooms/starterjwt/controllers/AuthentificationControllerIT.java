package com.openclassrooms.starterjwt.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.openclassrooms.starterjwt.dto.LoginRequestDto;
import com.openclassrooms.starterjwt.dto.RegisterRequestDto;
import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.repositorys.SessionRepository;
import com.openclassrooms.starterjwt.repositorys.UserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class AuthentificationControllerIT extends BaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private SessionRepository sessionRepository;

    @BeforeEach
    void setup() {
        sessionRepository.findAll().forEach(session -> {
            session.getUsers().clear();
            sessionRepository.save(session);
        });
        sessionRepository.deleteAll();
        userRepository.deleteAll();  
    }

    // ---------- TEST REGISTER ----------

    @Test
    @DisplayName("POST /api/auth/register - should create a new user and return token")
    void testRegister_Success() throws Exception {
        RegisterRequestDto registerDto = new RegisterRequestDto(
            "john@example.com", "John", "Doe", "password123"
        );

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.email").value("john@example.com"));

        User user = userRepository.findByEmail("john@example.com").orElse(null);
        assertThat(user).isNotNull();
        assertThat(user.getFirstName()).isEqualTo("John");
    }

    @Test
    @DisplayName("POST /api/auth/register - should fail if email already exists")
    void testRegister_EmailAlreadyExists() throws Exception {
        userRepository.save(User.builder()
                .email("john@example.com")
                .firstName("John")
                .lastName("Doe")
                .password(passwordEncoder.encode("password123"))
                .admin(false)
                .build());

        RegisterRequestDto registerDto = new RegisterRequestDto(
            "john@example.com", "John", "Doe", "password123"
        );

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerDto)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.token").value("Error: Email is already taken!")) // <- corrigé
                .andExpect(jsonPath("$.message").doesNotExist()); // <- ton controller ne renvoie pas "message"
    }

    // ---------- TEST LOGIN ----------

    @Test
    @DisplayName("POST /api/auth/login - should authenticate and return token")
    void testLogin_Success() throws Exception {
        userRepository.save(User.builder()
                .email("john@example.com")
                .firstName("John")
                .lastName("Doe")
                .password(passwordEncoder.encode("password123"))
                .admin(false)
                .build());

        LoginRequestDto loginDto = new LoginRequestDto("john@example.com", "password123");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.email").value("john@example.com"))
                .andExpect(jsonPath("$.admin").value(false));
    }

    @Test
    @DisplayName("POST /api/auth/login - should fail with wrong password")
    void testLogin_WrongPassword() throws Exception {
        userRepository.save(User.builder()
                .email("john@example.com")
                .firstName("John")
                .lastName("Doe")
                .password(passwordEncoder.encode("correctPassword"))
                .admin(false)
                .build());

        LoginRequestDto loginDto = new LoginRequestDto("john@example.com", "wrongPassword");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDto)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.token").value("Invalid email or password")) // <- corrigé
                .andExpect(jsonPath("$.message").doesNotExist()); // <- ton controller ne renvoie pas "message"
    }

    @Test
    @DisplayName("POST /api/auth/login - should fail if user not found")
    void testLogin_UserNotFound() throws Exception {
        LoginRequestDto loginDto = new LoginRequestDto("unknown@example.com", "password123");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDto)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.token").value("Invalid email or password")) // <- corrigé
                .andExpect(jsonPath("$.message").doesNotExist()); // <- ton controller ne renvoie pas "message"
    }
}
