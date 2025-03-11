package org.nova.backend.member.application.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
public class UpdateGraduationRequest {

    @Schema(description = "졸업년도", example = "2025")
    private int year;

    @Schema(description = "재직 여부", example = "false")
    private boolean work;

    @Schema(description = "직무", example = "개발자, 대학원생, 공기업")
    private String job;

    @Schema(description = "연락 공개 여부", defaultValue = "false")
    private boolean contact;

    @Schema(description = "연락처", example = "인스타그램 @nova")
    private String contactInfo;

    @Schema(description = "연락 방법 설명", example = "이메일 또는 인스타그램 디엠으로 연락주세요.")
    private String contactDescription;
}
