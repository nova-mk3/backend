package org.nova.backend.board.suggestion.domain.exception;

import org.nova.backend.shared.exception.CustomException;
import org.springframework.http.HttpStatus;

public class SuggestionFileDomainException extends CustomException {
    public SuggestionFileDomainException(String message, HttpStatus status) {
        super(message, status);
    }
}
