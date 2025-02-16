package org.nova.backend.board.clubArchive.domain.exception;

import org.nova.backend.shared.exception.CustomException;
import org.springframework.http.HttpStatus;

public class JokboDomainException extends CustomException {
    public JokboDomainException(String message, HttpStatus status) {
        super(message, status);
    }
}
