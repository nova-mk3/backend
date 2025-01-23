package org.nova.backend.board.domain.exception;

public class BoardDomainException extends RuntimeException {
    public BoardDomainException(String message) {
        super(message);
    }

    public BoardDomainException(String message, Throwable cause) {
        super(message, cause);
    }
}
