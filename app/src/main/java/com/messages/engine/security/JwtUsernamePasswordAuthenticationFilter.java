package com.messages.engine.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.messages.engine.dto.LoginRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * Custom authentication filter that reads login credentials from the request,
 * authenticates the user, and upon success generates a JWT token.
 *
 * This filter extends UsernamePasswordAuthenticationFilter and overrides
 * attemptAuthentication, successfulAuthentication, and unsuccessfulAuthentication.
 */
@Slf4j
@RequiredArgsConstructor
public class JwtUsernamePasswordAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {
        try {
            // Read and parse the login request JSON to a LoginRequest record
            LoginRequest loginRequest = objectMapper.readValue(request.getInputStream(), LoginRequest.class);
            log.info("Attempting authentication for user: {}", loginRequest.username());
            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(loginRequest.username(), loginRequest.password());
            return getAuthenticationManager().authenticate(authToken);
        } catch (IOException e) {
            log.error("Error parsing login request: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to parse login request", e);
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request,
                                            HttpServletResponse response,
                                            FilterChain chain,
                                            Authentication authResult)
            throws IOException, ServletException {
        // Generate JWT token upon successful authentication
        String token = jwtTokenProvider.generateToken(authResult);
        log.info("Authentication successful for user: {}. Token generated.", authResult.getName());
        response.setContentType("application/json");
        response.getWriter().write("{\"token\": \"" + token + "\"}");
        response.getWriter().flush();
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request,
                                              HttpServletResponse response,
                                              AuthenticationException failed)
            throws IOException, ServletException {
        log.warn("Authentication failed: {}", failed.getMessage());
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.getWriter().write("{\"error\": \"" + failed.getMessage() + "\"}");
        response.getWriter().flush();
    }
}
