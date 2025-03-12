package org.nova.backend.email.domain.exception;

import org.nova.backend.shared.exception.CustomException;
import org.springframework.http.HttpStatus;

public class EmailException extends CustomException {
    public EmailException(String message, HttpStatus status) {
        super(message, status);
    }
}
