package org.nova.backend.board.domain.exception;

public class CommentDomainException extends RuntimeException {
    public CommentDomainException(String message) {
        super(message);
    }

    public CommentDomainException(String message, Throwable cause) {
        super(message, cause);
    }
}
