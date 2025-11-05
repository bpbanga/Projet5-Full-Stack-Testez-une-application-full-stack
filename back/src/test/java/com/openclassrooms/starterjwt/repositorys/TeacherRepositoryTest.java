package com.openclassrooms.starterjwt.repositorys;

import com.openclassrooms.starterjwt.models.Teacher;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class TeacherRepositoryTest {

    @Autowired
    private TeacherRepository teacherRepository;

    @Test
    void testSaveAndFindById() {
        Teacher teacher = new Teacher();
        teacher.setFirstName("Alice");
        teacher.setLastName("Dupont");

        Teacher saved = teacherRepository.save(teacher);
        Optional<Teacher> found = teacherRepository.findById(saved.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getFirstName()).isEqualTo("Alice");
    }

    @Test
    void testFindAllAndDelete() {
        teacherRepository.save(new Teacher(null, "Jean", "Durand", null, null));
        teacherRepository.save(new Teacher(null, "Marie", "Curie", null, null));

        List<Teacher> all = teacherRepository.findAll();
        assertThat(all).hasSize(2);

        teacherRepository.deleteById(all.get(0).getId());
        assertThat(teacherRepository.findAll()).hasSize(1);
    }
}
