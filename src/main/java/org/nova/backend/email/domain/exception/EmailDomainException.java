package org.nova.backend.email.domain.exception;

public class EmailDomainException extends RuntimeException {

    public EmailDomainException(String message) {
        super(message);
    }

    public EmailDomainException(String message, Throwable cause) {
        super(message, cause);
    }
}
