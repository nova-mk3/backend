package org.nova.backend.auth.application.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
public class GraduationSignUpRequest {

    @Schema(description = "졸업 연도", example = "2025")
    private int year;

    @Schema(description = "연락 가능 여부", defaultValue = "false")
    private boolean contact;

    @Schema(description = "재직 여부", example = "false")
    private boolean work;

    @Schema(description = "직무", example = "개발자, 대학원생, 공기업")
    private String job;

    @Schema(description = "연락처", example = "인스타그램 @nova")
    private String contactInfo;

    @Schema(description = "연락 방법 설명", example = "이메일 또는 인스타그램 디엠으로 연락주세요.")
    private String contactDescription;
}
