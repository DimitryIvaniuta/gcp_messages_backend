package com.messages.engine.security;

import com.messages.engine.exception.JwtAuthenticationException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Filter that intercepts incoming HTTP requests, extracts a JWT token from the
 * Authorization header, validates it, and sets the corresponding Authentication in the
 * SecurityContext. This filter is applied once per request.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    /**
     * Utility class for generating and validating JWT tokens.
     */
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * Service for loading user-specific data during authentication.
     */
    private final UserDetailsService customUserDetailsService;

    /**
     * Extracts the JWT token from the Authorization header, validates it, and if valid,
     * loads the user details to set the authentication in the SecurityContext.
     *
     * @param request     the incoming HTTP request.
     * @param response    the HTTP response.
     * @param filterChain the filter chain.
     * @throws ServletException if an error occurs during the filtering.
     * @throws IOException      if an I/O error occurs.
     */
    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain)
            throws ServletException, IOException {
        try {
            String jwt = extractJwtFromRequest(request);
            if (StringUtils.hasText(jwt) && jwtTokenProvider.validateToken(jwt)) {
                String username = jwtTokenProvider.getUsernameFromJWT(jwt);
                UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);
                UsernamePasswordAuthenticationToken authenticationToken =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        } catch (JwtAuthenticationException ex) {
            log.error("Could not set user authentication in security context: {}", ex.getMessage(), ex);
        }
        filterChain.doFilter(request, response);
    }

    /**
     * Extracts the JWT token from the Authorization header in the HTTP request.
     *
     * @param request the HTTP request.
     * @return the JWT token as a String if present; otherwise, null.
     */
    private String extractJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

}
