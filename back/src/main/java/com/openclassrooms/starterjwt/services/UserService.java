package com.openclassrooms.starterjwt.services;

import com.openclassrooms.starterjwt.dto.RegisterRequestDto;
import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.repositorys.UserRepository;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Service responsable de la gestion des utilisateurs (création, recherche, suppression).
 */
@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Crée un nouvel utilisateur à partir d’un DTO d’inscription.
     *
     * @param request DTO contenant email, mot de passe, prénom et nom
     * @throws IllegalStateException si l’email est déjà pris
     */
    public void createUser(RegisterRequestDto request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalStateException("Error: Email is already taken!");
        }

        User user = User.builder()
                .email(request.getEmail())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .password(passwordEncoder.encode(request.getPassword()))
                .admin(false)
                .build();

        userRepository.save(user);
    }

    /**
     * Vérifie si un utilisateur existe par son email.
     */
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    /**
     * Supprime un utilisateur par son ID.
     */
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    /**
     * Récupère un utilisateur par son ID.
     */
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    /**
     * Récupère un utilisateur par son email.
     */
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
}
