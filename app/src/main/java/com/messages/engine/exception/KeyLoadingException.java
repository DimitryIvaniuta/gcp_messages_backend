package com.messages.engine.exception;

/**
 * Exception thrown when RSA keys fail to load from a PEM-formatted string.
 */
public class KeyLoadingException extends RuntimeException  {

    /**
     * Constructs a new KeyLoadingException with the specified detail message.
     *
     * @param message the detail message.
     */
    public KeyLoadingException(String message) {
        super(message);
    }

    /**
     * Constructs a new KeyLoadingException with the specified detail message and cause.
     *
     * @param message the detail message.
     * @param cause   the cause.
     */
    public KeyLoadingException(String message, Throwable cause) {
        super(message, cause);
    }

}
