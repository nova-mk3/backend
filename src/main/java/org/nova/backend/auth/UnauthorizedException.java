package org.nova.backend.auth;

import org.nova.backend.shared.exception.CustomException;
import org.springframework.http.HttpStatus;

public class UnauthorizedException extends CustomException {
    public UnauthorizedException(String message) {
        super(message, HttpStatus.FORBIDDEN);
    }

    public UnauthorizedException(String message, Throwable cause) {
        super(message, HttpStatus.FORBIDDEN);
        initCause(cause);
    }

    public UnauthorizedException(String message, HttpStatus status) {
        super(message, status);
    }

    public UnauthorizedException(String message, HttpStatus status, Throwable cause) {
        super(message, status);
        initCause(cause);
    }
}
