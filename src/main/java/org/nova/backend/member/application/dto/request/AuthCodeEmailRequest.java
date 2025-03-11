package org.nova.backend.member.application.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
public class AuthCodeEmailRequest {

    @Schema(example = "nova@chungbuk.ac.kr")
    private String email;
}
