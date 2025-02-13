package org.nova.backend.board.examarchive.domain.exception;

import org.nova.backend.shared.exception.CustomException;
import org.springframework.http.HttpStatus;

public class JokboDomainException extends CustomException {
    public JokboDomainException(String message, HttpStatus status) {
        super(message, status);
    }
}
