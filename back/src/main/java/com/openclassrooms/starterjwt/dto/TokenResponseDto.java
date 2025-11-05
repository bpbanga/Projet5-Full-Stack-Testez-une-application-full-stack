package com.openclassrooms.starterjwt.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Réponse renvoyée après l'authentification ou l'inscription
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TokenResponseDto {

    private String token;
    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private boolean admin;
}

