package com.openclassrooms.starterjwt.repositorys;

import com.openclassrooms.starterjwt.models.Session;
import com.openclassrooms.starterjwt.models.Teacher;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class SessionRepositoryTest {

    @Autowired
    private SessionRepository sessionRepository;

    @Test
    void testSaveAndFindById() {
        Session session = new Session();
        session.setName("Yoga Matin");
        session.setDescription("Session du matin");
        session.setDate(LocalDateTime.now());
        session.setTeacher(new Teacher());

        Session saved = sessionRepository.save(session);
        Optional<Session> found = sessionRepository.findById(saved.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Yoga Matin");
    }

    @Test
    void testFindAllAndDelete() {
        Session s1 = new Session();
        s1.setName("Session 1");
        s1.setDescription("Description 1");
        s1.setDate(LocalDateTime.now());

        Session s2 = new Session();
        s2.setName("Session 2");
        s2.setDescription("Description 2");
        s2.setDate(LocalDateTime.now());

        sessionRepository.save(s1);
        sessionRepository.save(s2);

        List<Session> sessions = sessionRepository.findAll();
        assertThat(sessions).hasSize(2);

        sessionRepository.deleteById(s1.getId());
        assertThat(sessionRepository.findAll()).hasSize(1);
    }
}
