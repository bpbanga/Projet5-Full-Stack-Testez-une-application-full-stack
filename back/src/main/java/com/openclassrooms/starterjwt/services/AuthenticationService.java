// package com.openclassrooms.starterjwt.services;


// import com.openclassrooms.starterjwt.dto.LoginRequestDto;
// import com.openclassrooms.starterjwt.dto.TokenResponseDto;
// import com.openclassrooms.starterjwt.exceptions.AuthenticatedUserNotFound;
// import com.openclassrooms.starterjwt.models.User;
// import com.openclassrooms.starterjwt.repositorys.UserRepository;
// import com.openclassrooms.starterjwt.security.JwtService;
// import com.openclassrooms.starterjwt.security.UserDetailsServiceImplementation;

// import org.springframework.security.authentication.AuthenticationManager;
// import org.springframework.security.authentication.AuthenticationServiceException;
// import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
// import org.springframework.security.core.Authentication;
// import org.springframework.security.core.AuthenticationException;
// import org.springframework.security.core.context.SecurityContextHolder;
// import org.springframework.stereotype.Service;

// /**
//  * Service gérant l'authentification et la génération du token JWT.
//  */
// @Service
// public class AuthenticationService {

//     private final AuthenticationManager authenticationManager;
//     private final JwtService jwtService;
//     private final UserRepository userRepository;

//     public AuthenticationService(AuthenticationManager authenticationManager,
//                                  JwtService jwtService,
//                                  UserRepository userRepository) {
//         this.authenticationManager = authenticationManager;
//         this.jwtService = jwtService;
//         this.userRepository = userRepository;
//     }

//     /**
//      * Authentifie un utilisateur à partir de son email et mot de passe,
//      * puis génère un token JWT s'il est valide.
//      */
//     public TokenResponseDto authenticate(LoginRequestDto request) {
//         try {
//             Authentication authentication = authenticationManager.authenticate(
//                     new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
//             );

//             SecurityContextHolder.getContext().setAuthentication(authentication);

//             // Génération du JWT
//             String token = jwtService.generateToken(authentication);

//             // Récupération des informations utilisateur
//             UserDetailsServiceImplementation userDetails = (UserDetailsServiceImplementation) authentication.getPrincipal();
//             User user = userRepository.findByEmail(userDetails.getUsername()).orElseThrow(() ->
//                     new AuthenticationServiceException("User not found in database after authentication."));

//             // Création du DTO de réponse
//             return new TokenResponseDto(
//                     token,
//                     user.getId(),
//                     user.getEmail(),
//                     user.getFirstName(),
//                     user.getLastName(),
//                     user.isAdmin()
//             );

//         } catch (AuthenticationException e) {
//             throw new AuthenticationServiceException("Authentication failed: " + e.getMessage(), e);
//         }
//     }

//     /**
//      * Récupère l'email de l'utilisateur actuellement authentifié.
//      */
//     public String getAuthenticatedUserEmail() {
//         Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

//         if (authentication != null && authentication.isAuthenticated()) {
//             return authentication.getName();
//         } else {
//             throw new AuthenticatedUserNotFound(
//                     "No authenticated user found in Security Context",
//                     "AuthenticationService.getAuthenticatedUserEmail"
//             );
//         }
//     }

//     /**
//      * Génère un token JWT directement à partir d'une Authentication existante.
//      * Utilisée par le contrôleur après login.
//      */
//     public TokenResponseDto generateToken(Authentication authentication) {
//         String token = jwtService.generateToken(authentication);

//         UserDetailsServiceImplementation userDetails = (UserDetailsServiceImplementation) authentication.getPrincipal();
//         User user = userRepository.findByEmail(userDetails.getUsername()).orElse(null);

//         if (user == null) {
//             throw new AuthenticationServiceException("Unable to find user for JWT generation.");
//         }

//         return new TokenResponseDto(
//                 token,
//                 user.getId(),
//                 user.getEmail(),
//                 user.getFirstName(),
//                 user.getLastName(),
//                 user.isAdmin()
//         );
//     }
// }

package com.openclassrooms.starterjwt.services;

import com.openclassrooms.starterjwt.dto.LoginRequestDto;
import com.openclassrooms.starterjwt.dto.TokenResponseDto;
import com.openclassrooms.starterjwt.exceptions.AuthenticatedUserNotFound;
import com.openclassrooms.starterjwt.models.AppUserDetails;
import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.repositorys.UserRepository;
import com.openclassrooms.starterjwt.security.JwtService;
import com.openclassrooms.starterjwt.security.UserDetailsServiceImplementation;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

/**
 * Service gérant l'authentification et la génération du token JWT.
 */
@Service
public class AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthenticationService(AuthenticationManager authenticationManager,
                                 JwtService jwtService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    public TokenResponseDto authenticate(LoginRequestDto request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            AppUserDetails userDetails = (AppUserDetails) authentication.getPrincipal();
            User user = userDetails.getUser();

            String token = jwtService.generateToken(authentication);

            return new TokenResponseDto(
                    token,
                    user.getId(),
                    user.getEmail(),
                    user.getFirstName(),
                    user.getLastName(),
                    user.isAdmin()
            );

        } catch (AuthenticationException e) {
            throw new AuthenticationServiceException("Authentication failed: " + e.getMessage(), e);
        }
    }

    public TokenResponseDto generateToken(Authentication authentication) {
        AppUserDetails userDetails = (AppUserDetails) authentication.getPrincipal();
        User user = userDetails.getUser();
            
        if (user == null) {
            throw new AuthenticationServiceException("Unable to find user for JWT generation.");
        }

        String token = jwtService.generateToken(authentication);

        return new TokenResponseDto(
                token,
                user.getId(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.isAdmin()
        );
    }

    public String getAuthenticatedUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated()) {
            return authentication.getName();
        } else {
            throw new AuthenticatedUserNotFound(
                    "No authenticated user found in Security Context",
                    "AuthenticationService.getAuthenticatedUserEmail"
            );
        }
    }
}


