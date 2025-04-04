package com.messages.engine.exception;

/**
 * Exception thrown when JWT authentication-related operations fail.
 */
public class JwtAuthenticationException extends RuntimeException {

    /**
     * Constructs a new JwtAuthenticationException with the specified detail message.
     *
     * @param message the detail message.
     */
    public JwtAuthenticationException(String message) {
        super(message);
    }

    /**
     * Constructs a new JwtAuthenticationException with the specified detail message and cause.
     *
     * @param message the detail message.
     * @param cause   the cause.
     */
    public JwtAuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }

}
