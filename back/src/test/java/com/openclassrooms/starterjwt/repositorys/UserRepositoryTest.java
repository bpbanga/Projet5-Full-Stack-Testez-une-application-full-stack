package com.openclassrooms.starterjwt.repositorys;

import com.openclassrooms.starterjwt.models.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void testSaveAndFindByEmail() {
        User user = User.builder()
                .email("john.doe@example.com")
                .firstName("John")
                .lastName("Doe")
                .password("encodedPassword")
                .admin(false)
                .build();

        userRepository.save(user);

        Optional<User> found = userRepository.findByEmail("john.doe@example.com");
        assertThat(found).isPresent();
        assertThat(found.get().getFirstName()).isEqualTo("John");
    }

    @Test
    void testExistsByEmail_ShouldReturnTrue_WhenUserExists() {
        User user = User.builder()
                .email("exists@example.com")
                .firstName("Exist")
                .lastName("User")
                .password("password")
                .admin(false)
                .build();

        userRepository.save(user);

        boolean exists = userRepository.existsByEmail("exists@example.com");
        assertThat(exists).isTrue();
    }

    @Test
    void testExistsByEmail_ShouldReturnFalse_WhenUserNotExists() {
        boolean exists = userRepository.existsByEmail("nope@example.com");
        assertThat(exists).isFalse();
    }
}
