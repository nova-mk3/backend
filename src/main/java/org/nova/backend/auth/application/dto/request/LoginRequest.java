package org.nova.backend.auth.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class LoginRequest {

    @NotBlank(message = "학번을 입력해주세요.")
    private String studentNumber;

    @NotBlank(message = "비밀번호를 입력해주세요.")
    private String password;

}
