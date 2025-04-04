package com.messages.engine.config;

import com.messages.engine.security.CustomUserDetailsService;
import com.messages.engine.security.JwtAuthenticationFilter;
import com.messages.engine.security.JwtTokenProvider;
import com.messages.engine.security.JwtUsernamePasswordAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

/**
 * Security configuration using Spring Security 6's modern DSL.
 * <p>
 * This configuration disables CSRF, configures stateless session management,
 * and permits public access to authentication endpoints (e.g. signup/signin).
 * All other endpoints require authentication.
 * A JwtAuthenticationFilter is added to validate JWT tokens in incoming requests.
 * </p>
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;

    private final JwtAuthenticationFilter jwtAuthenticationFilter; // For validating JWT on subsequent requests

    /**
     * Configures the HTTP security filter chain using the new lambda DSL.
     *
     * @param http the HttpSecurity instance.
     * @return the configured SecurityFilterChain.
     * @throws Exception if an error occurs.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, AuthenticationManager authManager) throws Exception {
        // Custom login filter instance and set the authentication manager
        JwtUsernamePasswordAuthenticationFilter loginFilter = new JwtUsernamePasswordAuthenticationFilter(jwtTokenProvider);
        loginFilter.setAuthenticationManager(authManager);
        // Set the login URL for our filter; for example: /api/auth/login
        loginFilter.setFilterProcessesUrl("/api/auth/login");

        http
                // Enable CSRF protection and store the CSRF token in a cookie.
                .csrf(csrf -> csrf.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()))
                // Configure session management to be stateless.
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // Define authorization rules:
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/signup", "/api/auth/login").permitAll()
                        .anyRequest().authenticated())
                // Custom login filter at the position of the default UsernamePasswordAuthenticationFilter.
                .addFilterAt(loginFilter, UsernamePasswordAuthenticationFilter.class)
                // Also add our JWT authentication filter before the UsernamePasswordAuthenticationFilter
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()));

        return http.build();
    }

    /**
     * Provides a PasswordEncoder bean using BCrypt.
     *
     * @return a BCryptPasswordEncoder instance.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Exposes the AuthenticationManager bean from the AuthenticationConfiguration.
     *
     * @param authConfig the AuthenticationConfiguration.
     * @return the AuthenticationManager.
     * @throws Exception if an error occurs.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

}
