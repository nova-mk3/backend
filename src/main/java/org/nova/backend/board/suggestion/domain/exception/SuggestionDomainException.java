package org.nova.backend.board.suggestion.domain.exception;

import org.nova.backend.shared.exception.CustomException;
import org.springframework.http.HttpStatus;

public class SuggestionDomainException extends CustomException {
    public SuggestionDomainException(String message, HttpStatus status) {
        super(message, status);
    }
}