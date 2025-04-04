package com.messages.engine.dto;

/**
 * Immutable DTO for JWT authentication responses.
 *
 * @param accessToken the JWT access token.
 * @param tokenType   the token type (e.g., "Bearer").
 */
public record JwtAuthenticationResponse(String accessToken, String tokenType) { }
