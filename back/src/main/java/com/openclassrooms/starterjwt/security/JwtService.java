package com.openclassrooms.starterjwt.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;


@Service
public class JwtService {

    private final HmacJwtFactory jwtFactory;

    public JwtService(HmacJwtFactory jwtFactory) {
        this.jwtFactory = jwtFactory;
    }

    public String generateToken(Authentication authentication) {
        return jwtFactory.generateToken(authentication);
    }

    public String extractUsername(String token) {
        return jwtFactory.extractUsername(token);
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        return extractUsername(token).equals(userDetails.getUsername());
    }
}