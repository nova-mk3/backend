package org.nova.backend.auth.application.dto.request;

import lombok.Getter;

@Getter
public class PasswordResetRequest {
    private String name;
    private String email;
}
