package org.nova.backend.board.domain.exception;

public class FileDomainException extends RuntimeException {
    public FileDomainException(String message) {
        super(message);
    }

    public FileDomainException(String message, Throwable cause) {
        super(message, cause);
    }
}
