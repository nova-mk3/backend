package org.nova.backend.member.domain.exception;

import org.nova.backend.shared.exception.CustomException;
import org.springframework.http.HttpStatus;

public class ExecutiveHistoryDomainException extends CustomException {

    public ExecutiveHistoryDomainException(String message, HttpStatus status) {
        super(message, status);
    }
}
