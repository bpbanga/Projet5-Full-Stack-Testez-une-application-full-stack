package com.openclassrooms.starterjwt.configurations;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.openclassrooms.starterjwt.security.HmacJwtFactory;


/**
 * Configuration class for JWT factory setup.
 *
 * This class provides a bean for creating an instance of {@link HmacJwtFactory},
 * which is responsible for encoding and decoding JWT tokens using HMAC-based signing.
 * The secret key used for token generation is injected from the application properties.
 *
 * @author Pagès
 */
@Configuration
public class JwtFactoryConfig {

    /**
     * The secret key used for HMAC-based JWT signing.
     * Injected from the application configuration (e.g., application.properties).
     */
    @Value("${jwt.secret}")
    private String secret;

    /**
     * Creates and exposes a singleton {@link HmacJwtFactory} bean.
     *
     * @return an instance of {@code HmacJwtFactory} initialized with the configured secret
     */
    @Bean
    public HmacJwtFactory jwtFactory() {
        return new HmacJwtFactory(secret);
    }
}
