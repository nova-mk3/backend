package org.nova.backend.email.domain.exception;

public class EmailAuthException extends RuntimeException {

    public EmailAuthException(String message) {
        super(message);
    }

    public EmailAuthException(String message, Throwable cause) {
        super(message, cause);
    }
}
