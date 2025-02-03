package org.nova.backend.member.domain.exception;

import org.nova.backend.shared.exception.CustomException;
import org.springframework.http.HttpStatus;

public class PendingMemberDomainException extends CustomException {

    public PendingMemberDomainException(String message, HttpStatus status) {
        super(message, status);
    }
}
