package org.nova.backend.board.clubArchive.domain.exception;

import org.nova.backend.shared.exception.CustomException;
import org.springframework.http.HttpStatus;

public class PictureDomainException extends CustomException {
    public PictureDomainException(String message, HttpStatus status) {
        super(message, status);
    }
}
