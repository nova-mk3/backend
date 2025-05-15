package org.nova.backend.notification.domain.exception;

import org.nova.backend.shared.exception.CustomException;
import org.springframework.http.HttpStatus;

public class NotificationDomainException extends CustomException {
    public NotificationDomainException(String message, HttpStatus status) {
        super(message, status);
    }
}
