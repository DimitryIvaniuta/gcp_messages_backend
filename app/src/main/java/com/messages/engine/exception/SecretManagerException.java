package com.messages.engine.exception;

/**
 * Exception thrown when there is an error accessing a secret from Google Cloud Secret Manager.
 */
public class SecretManagerException extends RuntimeException {

    /**
     * Constructs a new SecretManagerException with the specified detail message.
     *
     * @param message the detail message.
     */
    public SecretManagerException(String message) {
        super(message);
    }

    /**
     * Constructs a new SecretManagerException with the specified detail message and cause.
     *
     * @param message the detail message.
     * @param cause   the cause of the exception.
     */
    public SecretManagerException(String message, Throwable cause) {
        super(message, cause);
    }

}
