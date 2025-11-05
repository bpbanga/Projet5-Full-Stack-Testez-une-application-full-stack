package com.openclassrooms.starterjwt.configurations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.openclassrooms.starterjwt.security.JwtAuthenticationFilter;

import jakarta.servlet.http.HttpServletResponse;

import com.openclassrooms.starterjwt.security.HmacJwtFactory;


/**
 * Spring Boot Security Configuration.
 *
 * This class sets up authentication and authorization mechanisms using Spring Security.
 * It integrates a custom JWT filter, defines public routes, configures CORS,
 * and manages password encoders and authentication providers.
 *
 * Key features:
 * <ul>
 *   <li>Stateless authentication using JWT</li>
 *   <li>HTTP request filtering via JwtAuthenticationFilter</li>
 *   <li>Definition of publicly accessible endpoints</li>
 *   <li>CORS configuration to allow requests from the Angular frontend</li>
 *   <li>Integration of a DAO-based AuthenticationProvider using UserDetailsService</li>
 *   <li>Activation of Swagger UI and API documentation endpoints</li>
 * </ul>
 *
 * @author Pagès
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private final HmacJwtFactory jwtFactory;
    private final JwtAuthenticationFilter jwtAuthFilter;
    private final UserDetailsService userDetailsService;

    /**
     * List of endpoints that are publicly accessible without authentication.
     */
    private static final String[] AUTH_WHITELIST = {
        "/api/auth/login",
        "/api/auth/register",
        "/api/files/**",
        "/api/swagger-ui/**",
        "/error"
    };

    /**
     * Constructor for injecting required security components.
     *
     * @param jwtAuthFilter the custom JWT authentication filter
     * @param userDetailsService the service used to load user-specific data
     * @param jwtFactory the factory for encoding and decoding JWT tokens
     */
    public SecurityConfig(@Lazy JwtAuthenticationFilter jwtAuthFilter,
                          UserDetailsService userDetailsService,
                          HmacJwtFactory jwtFactory) {
        this.jwtAuthFilter = jwtAuthFilter;
        this.userDetailsService = userDetailsService;
        this.jwtFactory = jwtFactory;
    }

    /**
     * Provides a BCrypt password encoder bean.
     *
     * @return a BCryptPasswordEncoder instance
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Configures the security filter chain for HTTP requests.
     *
     * @param http the HttpSecurity object to configure
     * @return the configured SecurityFilterChain
     * @throws Exception if an error occurs during configuration
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable());
        http.cors();

        http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(AUTH_WHITELIST).permitAll()
                .anyRequest().authenticated())
            .httpBasic(Customizer.withDefaults())
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
            .authenticationProvider(authenticationProvider())
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint((request, response, authException) -> {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
                })
            );


        return http.build();
    }

    /**
     * Provides the authentication provider using DAO and BCrypt.
     *
     * @return the configured AuthenticationProvider
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }



    /**
     * Builds the authentication manager using the configured user details service and password encoder.
     *
     * @param http the HttpSecurity object
     * @param passwordEncoder the password encoder to use
     * @return the configured AuthenticationManager
     * @throws Exception if an error occurs during setup
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    /**
     * Provides the JWT encoder bean using the configured factory.
     *
     * @return a JwtEncoder instance
     */
    @Bean
    public JwtEncoder jwtEncoder() {
        return jwtFactory.getJwtEncoder();
    }

    /**
     * Provides the JWT decoder bean using the configured factory.
     *
     * @return a JwtDecoder instance
     */
    @Bean
    public JwtDecoder jwtDecoder() {
        return jwtFactory.getJwtDecoder();
    }

    /**
     * Configures CORS to allow requests from the Angular frontend.
     *
     * @return the configured CorsConfigurationSource
     */
    @Bean
    public org.springframework.web.cors.CorsConfigurationSource corsConfigurationSource() {
        org.springframework.web.cors.CorsConfiguration configuration = new org.springframework.web.cors.CorsConfiguration();
        configuration.setAllowedOrigins(java.util.List.of("http://localhost:4200"));
        configuration.setAllowedMethods(java.util.List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(java.util.List.of("*"));
        configuration.setAllowCredentials(true);

        org.springframework.web.cors.UrlBasedCorsConfigurationSource source =
            new org.springframework.web.cors.UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
