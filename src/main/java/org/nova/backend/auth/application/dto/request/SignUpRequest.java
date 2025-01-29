package org.nova.backend.auth.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import org.nova.backend.member.domain.model.valueobject.Role;

@Getter
public class SignUpRequest {

    @NotBlank(message = "학번을 입력해주세요.")
    private String studentNumber;

    @NotBlank(message = "비밀번호를 입력해주세요.")
    private String password;

    @NotBlank(message = "이름을 입력해주세요.")
    private String name;

    @Pattern(regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+.[A-Za-z]{2,6}$", message = "이메일 형식에 맞지 않습니다.")
    @NotBlank(message = "이름을 입력해주세요.")
    private String email;

    private boolean isGraduation;

    private int year;

    private int semester;

    private boolean isAbsence;

    private String profilePhoto;

    private String phone;

    private String introduction;

    private String birth;

    private Role role;

}
