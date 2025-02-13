package org.nova.backend.auth.application.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import org.nova.backend.member.domain.model.valueobject.Role;

@Getter
public class MemberSignUpRequest {

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

    @Schema(example = "노바")
    @NotBlank(message = "이름을 입력해주세요.")
    private String name;

    @Schema(example = "nova@chungbuk.ac.kr")
    @Pattern(regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+.[A-Za-z]{2,6}$", message = "이메일 형식에 맞지 않습니다.")
    @NotBlank(message = "이메일을 입력해주세요.")
    private String email;

    private boolean graduation;

    @Schema(description = "학년", example = "1")
    private int grade;

    @Schema(description = "이수 학기", example = "1")
    private int semester;

    private boolean absence;

    private String profilePhoto;

    @Schema(description = "(-)없이 전화번호를 입력해주세요.", example = "01000000000")
    private String phone;

    @Schema(example = "20000202")
    private String birth;

}
