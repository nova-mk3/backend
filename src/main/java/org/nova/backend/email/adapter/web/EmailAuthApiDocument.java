package org.nova.backend.email.adapter.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Tag(name = "Email API", description = "이메일 인증 코드 전송 관련 API 목록")
public @interface EmailAuthApiDocument {

    @Operation(summary = "이메일 인증 코드 전송", description = "사용자 이메일로 인증 코드를 전송합니다. EmailAuth 를 생성합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "이메일 인증코드 전송 성공",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "500", content = @Content(mediaType = "application/json",
                    examples = {@ExampleObject(name = "이메일 전송 실패",
                            value = "{\"code\": 500, \"message\": \"email send failed. nova@chungbuk.ac.kr\"}"),
                            @ExampleObject(name = "서버 오류",
                                    value = "{\"code\": 500, \"message\": \"internal server error\"}")
                    }
            ))
    })
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @interface EmailAuthApiDoc {
    }

    @Operation(summary = "인증 코드 확인", description = "이메일로 전송된 인증 코드를 확인합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "인증 코드 확인 완료.",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "500", content = @Content(mediaType = "application/json",
                    examples = {@ExampleObject(name = "인증 코드 확인 실패",
                            value = "{\"code\": 500, \"message\": \"email verification failed. nova@chungbuk.ac.kr\"}"),
                            @ExampleObject(name = "서버 오류",
                                    value = "{\"code\": 500, \"message\": \"internal server error\"}")
                    }
            ))
    })
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @interface CheckAuthCodeApiDoc {
    }
}
