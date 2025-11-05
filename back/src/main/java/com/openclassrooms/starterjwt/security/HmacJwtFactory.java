package com.openclassrooms.starterjwt.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import com.nimbusds.jose.jwk.source.ImmutableSecret;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

public class HmacJwtFactory {

    private final SecretKey secretKey;
    private final JwtEncoder jwtEncoder;
    private final JwtDecoder jwtDecoder;

    public HmacJwtFactory(String secret) {
        if (secret == null || secret.length() < 32) {
            throw new IllegalArgumentException("Secret must be at least 32 characters");
        }
        this.secretKey = new SecretKeySpec(secret.getBytes(), "HmacSHA256");
        this.jwtEncoder = new NimbusJwtEncoder(new ImmutableSecret<>(secretKey));
        this.jwtDecoder = NimbusJwtDecoder.withSecretKey(secretKey).macAlgorithm(MacAlgorithm.HS256).build();
    }

    public String generateToken(Authentication authentication) {
        Instant now = Instant.now();
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("self")
                .issuedAt(now)
                .expiresAt(now.plus(1, ChronoUnit.HOURS))
                .subject(authentication.getName())
                .claim("roles", "USER")
                .build();

        JwtEncoderParameters params = JwtEncoderParameters.from(JwsHeader.with(MacAlgorithm.HS256).build(), claims);
        return jwtEncoder.encode(params).getTokenValue();
    }

    public String extractUsername(String token) {
        return jwtDecoder.decode(token).getSubject();
    }

    public boolean validateToken(String token, String expectedUsername) {
        return extractUsername(token).equals(expectedUsername);
    }

    public JwtEncoder getJwtEncoder() {
        return jwtEncoder;
    }

    public JwtDecoder getJwtDecoder() {
        return jwtDecoder;
    }
}
