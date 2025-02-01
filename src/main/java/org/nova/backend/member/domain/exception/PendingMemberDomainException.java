package org.nova.backend.member.domain.exception;

public class PendingMemberDomainException extends RuntimeException {

    public PendingMemberDomainException(String message) {
        super(message);
    }

    public PendingMemberDomainException(String message, Throwable cause) {
        super(message, cause);
    }
}
