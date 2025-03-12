package org.nova.backend.member.domain.exception;

import org.nova.backend.shared.exception.CustomException;
import org.springframework.http.HttpStatus;

public class MemberDomainException extends CustomException {

    public MemberDomainException(String message, HttpStatus status) {
        super(message, status);
    }
}
