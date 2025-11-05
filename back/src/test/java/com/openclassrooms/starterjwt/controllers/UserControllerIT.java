package com.openclassrooms.starterjwt.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
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

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test d'intégration complet du UserController.
 * Utilise MockMvc et une base H2 en mémoire.
 */
class UserControllerIT extends BaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SessionRepository sessionRepository;


    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private User user1;
    private User user2;
    private String jwtToken;

    @BeforeEach
void setup() throws Exception {
    sessionRepository.findAll().forEach(session -> {
        session.getUsers().clear();
        sessionRepository.save(session);
    });
    sessionRepository.deleteAll();
    userRepository.deleteAll();

    user1 = new User();
    user1.setFirstName("John");
    user1.setLastName("Doe");
    user1.setEmail("john.doe@example.com");
    user1.setPassword(passwordEncoder.encode("123456"));
    user1.setCreatedAt(LocalDateTime.now());
    user1.setUpdatedAt(LocalDateTime.now());
    userRepository.save(user1);

    user2 = new User();
    user2.setFirstName("Jane");
    user2.setLastName("Smith");
    user2.setEmail("jane.smith@example.com");
    user2.setPassword(passwordEncoder.encode("abcdef"));
    user2.setCreatedAt(LocalDateTime.now());
    user2.setUpdatedAt(LocalDateTime.now());
    userRepository.save(user2);

    User admin = new User();
    admin.setEmail("admin@example.com");
    admin.setFirstName("Admin");
    admin.setLastName("User");
    admin.setPassword(passwordEncoder.encode("password"));
    admin.setAdmin(true);
    userRepository.save(admin);

    Map<String, String> login = new HashMap<>();
    login.put("email", "john.doe@example.com");
    login.put("password", "123456");

    var mvcResult = mockMvc.perform(post("/api/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(login)))
            .andExpect(status().isOk())
            .andReturn();

    jwtToken = objectMapper.readTree(mvcResult.getResponse().getContentAsString())
                           .get("token").asText();
}

    @Test
    @DisplayName("GET /api/user/{id} → doit retourner 200 et l'utilisateur")
    void testFindById_UserExists() throws Exception {
        mockMvc.perform(get("/api/user/{id}", user1.getId())
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(user1.getId()))
                .andExpect(jsonPath("$.email").value("john.doe@example.com"))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"));
    }

    @Test
    @DisplayName("GET /api/user/{id} → doit retourner 404 si utilisateur introuvable")
    void testFindById_UserNotFound() throws Exception {
        mockMvc.perform(get("/api/user/{id}", 999L)
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /api/user/{id} → doit retourner 400 si ID invalide")
    void testFindById_InvalidId() throws Exception {
        mockMvc.perform(get("/api/user/{id}", "invalid")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("DELETE /api/user/{id} → utilisateur autorisé doit pouvoir supprimer son compte")
    void testDelete_UserAuthorized() throws Exception {
        mockMvc.perform(delete("/api/user/{id}", user1.getId())
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk());

        boolean stillExists = userRepository.findById(user1.getId()).isPresent();
        assert !stillExists;
    }

    @Test
    @DisplayName("DELETE /api/user/{id} → doit retourner 404 si utilisateur introuvable")
    void testDelete_UserNotFound() throws Exception {
        mockMvc.perform(delete("/api/user/{id}", 999L)
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isNotFound());
    }
}
