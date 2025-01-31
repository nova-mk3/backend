package org.nova.backend.auth.application.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;

@Getter
public class LoginRequest {

    @Schema(example = "20202020")
    @NotBlank(message = "학번을 입력해주세요.")
    private String studentNumber;

    @Schema(example = "12abAB!@")
    @Pattern(
            regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\W).{8,16}$",
            message = "비밀번호는 8~16자 이내이며, 대문자, 소문자, 특수문자를 포함해야 합니다."
    )
    @NotBlank(message = "비밀번호를 입력해주세요.")
    private String password;

}
