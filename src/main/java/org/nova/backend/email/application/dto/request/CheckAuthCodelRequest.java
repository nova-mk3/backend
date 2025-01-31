package org.nova.backend.email.application.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
public class CheckAuthCodelRequest {

    @Schema(example = "nova@chungbuk.ac.kr")
    private String email;

    private String authCode;
}
