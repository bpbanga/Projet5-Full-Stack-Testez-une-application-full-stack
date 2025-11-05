package com.openclassrooms.starterjwt.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.openclassrooms.starterjwt.models.Teacher;
import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.repositorys.TeacherRepository;
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

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

/**
 * Test d'intégration du contrôleur TeacherController.
 * On utilise ici la vraie couche web + base H2 configurée pour les tests.
 */
class TeacherControllerIT extends BaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private ObjectMapper objectMapper;
    
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private Teacher teacher1;
    private Teacher teacher2;

    private String jwtToken;

    @BeforeEach
    void setup() throws Exception {
        teacherRepository.deleteAll();

        teacher1 = new Teacher();
        teacher1.setFirstName("John");
        teacher1.setLastName("Doe");
        teacher1.setCreatedAt(LocalDateTime.now());
        teacher1.setUpdatedAt(LocalDateTime.now());
        teacher1 = teacherRepository.save(teacher1);

        teacher2 = new Teacher();
        teacher2.setFirstName("Jane");
        teacher2.setLastName("Smith");
        teacher2.setCreatedAt(LocalDateTime.now());
        teacher2.setUpdatedAt(LocalDateTime.now());
        teacher2 = teacherRepository.save(teacher2);

            // Créer un utilisateur admin pour le test
        User user = new User();
        user.setEmail("admin@example.com");
        user.setFirstName("Admin");
        user.setLastName("User");
        user.setPassword(passwordEncoder.encode("password"));
        user.setAdmin(true);
        userRepository.save(user);

         // Login pour récupérer le token
    Map<String, String> login = new HashMap<>();
    login.put("email", "admin@example.com");
    login.put("password", "password");

    var mvcResult = mockMvc.perform(post("/api/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(login)))
            .andExpect(status().isOk())
            .andReturn();

    jwtToken = objectMapper.readTree(mvcResult.getResponse().getContentAsString())
                           .get("token").asText();
    }

    @Test
    @DisplayName("GET /api/teacher/{id} → doit retourner 200 et le bon enseignant")
    void testFindById_Found() throws Exception {
        mockMvc.perform(get("/api/teacher/{id}", teacher1.getId())
                        .header("Authorization", "Bearer " + jwtToken)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(teacher1.getId()))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"));
    }

    @Test
    @DisplayName("GET /api/teacher/{id} → doit retourner 404 si enseignant inexistant")
    void testFindById_NotFound() throws Exception {
        mockMvc.perform(get("/api/teacher/{id}", 999L)
                        .header("Authorization", "Bearer " + jwtToken)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /api/teacher/{id} → doit retourner 400 si id invalide")
    void testFindById_BadRequest() throws Exception {
        mockMvc.perform(get("/api/teacher/{id}", "invalid-id")
                        .header("Authorization", "Bearer " + jwtToken)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /api/teacher → doit retourner 200 et la liste complète des enseignants")
    void testFindAll() throws Exception {
        mockMvc.perform(get("/api/teacher")
                       .header("Authorization", "Bearer " + jwtToken)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[*].firstName", containsInAnyOrder("John", "Jane")));
    }
}
