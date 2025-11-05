package com.openclassrooms.starterjwt.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.openclassrooms.starterjwt.models.Session;
import com.openclassrooms.starterjwt.models.Teacher;
import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.repositorys.SessionRepository;
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
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class SessionControllerIT extends BaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private SessionRepository sessionRepository;

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private Session session;
    private Teacher teacher;
    private String jwtToken;

    @BeforeEach
    void setup() throws Exception {
        // Clean DB
        sessionRepository.deleteAll();
        teacherRepository.deleteAll();
        userRepository.deleteAll();

        // Créer un utilisateur test et récupérer le token
        User user = new User();
        user.setEmail("test@example.com");
        user.setFirstName("Test");
        user.setLastName("User");
        user.setPassword(passwordEncoder.encode("password"));
        user.setAdmin(true);
        userRepository.save(user);

        Map<String, String> login = new HashMap<>();
        login.put("email", "test@example.com");
        login.put("password", "password");

        var mvcResult = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isOk())
                .andReturn();

        jwtToken = objectMapper.readTree(mvcResult.getResponse().getContentAsString())
                               .get("token").asText();

        // Créer un teacher
        teacher = new Teacher();
        teacher.setFirstName("John");
        teacher.setLastName("Doe");
        teacher.setCreatedAt(LocalDateTime.now());
        teacher = teacherRepository.save(teacher);

        // Créer une session initiale
        session = new Session();
        session.setName("Yoga Matin");
        session.setDescription("Séance matinale de yoga");
        session.setDate(LocalDateTime.now());
        session.setTeacher(teacher);
        session = sessionRepository.save(session);
    }

    // ---------- TEST FIND BY ID ----------

    @Test
    @DisplayName("GET /api/session/{id} - should return session when found")
    void testFindById_Found() throws Exception {
        mockMvc.perform(get("/api/session/{id}", session.getId())
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(session.getId()))
                .andExpect(jsonPath("$.name").value("Yoga Matin"));
    }

    @Test
    @DisplayName("GET /api/session/{id} - should return 404 when not found")
    void testFindById_NotFound() throws Exception {
        mockMvc.perform(get("/api/session/{id}", 999)
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /api/session/{id} - should return 400 for invalid id format")
    void testFindById_BadRequest() throws Exception {
        mockMvc.perform(get("/api/session/{id}", "abc")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isBadRequest());
    }

    // ---------- TEST FIND ALL ----------

    @Test
    @DisplayName("GET /api/session - should return all sessions")
    void testFindAll() throws Exception {
        mockMvc.perform(get("/api/session")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Yoga Matin"));
    }

    // ---------- TEST CREATE ----------

   @Test
@DisplayName("POST /api/session - should create session successfully")
void testCreate() throws Exception {
    // Créer un nouveau teacher spécifique pour cette session
    Teacher newTeacher = new Teacher();
    newTeacher.setFirstName("Jane");
    newTeacher.setLastName("Doe");
    newTeacher.setCreatedAt(LocalDateTime.now());
    newTeacher = teacherRepository.save(newTeacher);

    // Préparer le body de la nouvelle session
    Map<String, Object> newSession = new HashMap<>();
    newSession.put("name", "Zumba Soir");
    newSession.put("description", "Cours de zumba du soir");
    newSession.put("date", LocalDateTime.now().plusDays(1).toString());
    newSession.put("teacher_id", newTeacher.getId());

    // Appel POST /api/session avec JWT
    mockMvc.perform(post("/api/session")
                    .header("Authorization", "Bearer " + jwtToken)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(newSession)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.name").value("Zumba Soir"))
            .andExpect(jsonPath("$.teacher_id").value(newTeacher.getId()));

    // Vérifier en base
    List<Session> sessions = sessionRepository.findAll();
    assertThat(sessions).hasSize(2); // la session initiale + nouvelle
    assertThat(sessions.get(1).getName()).isEqualTo("Zumba Soir");
}
    // ---------- TEST DELETE ----------

    @Test
    @DisplayName("DELETE /api/session/{id} - should delete session successfully")
    void testDelete_Success() throws Exception {
        mockMvc.perform(delete("/api/session/{id}", session.getId())
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isNoContent());

        assertThat(sessionRepository.findById(session.getId())).isEmpty();
    }

    @Test
    @DisplayName("DELETE /api/session/{id} - should return 404 if not found")
    void testDelete_NotFound() throws Exception {
        mockMvc.perform(delete("/api/session/{id}", 999)
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isNotFound());
    }
}
