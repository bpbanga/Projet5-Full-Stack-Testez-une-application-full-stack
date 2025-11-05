package com.openclassrooms.starterjwt.repositorys;

import com.openclassrooms.starterjwt.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository JPA pour la gestion des utilisateurs.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Vérifie si un utilisateur existe déjà avec cet email.
     */
    boolean existsByEmail(String email);

    /**
     * Recherche un utilisateur par email.
     */
    Optional<User> findByEmail(String email);
}
