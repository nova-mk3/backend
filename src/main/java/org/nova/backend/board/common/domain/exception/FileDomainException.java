package org.nova.backend.board.common.domain.exception;

import org.nova.backend.shared.exception.CustomException;
import org.springframework.http.HttpStatus;

public class FileDomainException extends CustomException {
    public FileDomainException(String message, HttpStatus status) {
        super(message, status);
    }

    public FileDomainException(String message, HttpStatus status, Throwable cause) {
        super(message, status);
        initCause(cause);
    }
}
