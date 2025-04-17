package org.nova.backend.member.application.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class UpdateMemberProfileRequest {

    @Schema(example = "노바")
    @NotBlank(message = "이름을 입력해주세요.")
    private String name;

    @Schema(description = "졸업 여부")
    private boolean graduation;

    @Schema(description = "학년", example = "1학년")
    private String grade;

    @Schema(description = "학기", example = "1학기")
    private String semester;

    @Schema(description = "휴학")
    private boolean absence;

    @Schema(example = "20000202")
    private String birth;

    @Schema(description = "(-)없이 전화번호를 입력해주세요.", example = "01000000000")
    private String phone;

    @Schema(example = "안녕하세요 ^O^")
    private String introduction;

}
