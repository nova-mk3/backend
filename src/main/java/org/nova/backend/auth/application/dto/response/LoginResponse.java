package org.nova.backend.auth.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LoginResponse {
    private String message;
    private boolean isTempPassword;
}