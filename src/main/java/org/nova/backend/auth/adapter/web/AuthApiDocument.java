package org.nova.backend.auth.adapter.web;

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

@Tag(name = "회원 인증 API", description = "회원가입 로그인 API")
public @interface AuthApiDocument {

    @Operation(summary = "회원가입", description = "새로운 회원 가입 요청을 생성합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "새로운 회원 가입 요청 생성 성공",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "500", content = @Content(mediaType = "application/json",
                    examples = {@ExampleObject(name = "학번 or 이메일 중복",
                            value = "{\"code\": 500, \"message\": \"Member already exists. check student number or email 20202020 nova@chungbuk.ac.kr\"}"),
                            @ExampleObject(name = "서버 오류",
                                    value = "{\"code\": 500, \"message\": \"Member not found 20202020\"}")}
            ))
    })
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @interface SignUpApiDoc {
    }

    @Operation(summary = "로그인", description = "회원가입된 정보로 본인 인증 로그인")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "로그인 성공",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "401", content = @Content(mediaType = "application/json",
                    examples = {@ExampleObject(name = "로그인 실패. 학번 또는 비밀번호를 다시 확인해주세요.",
                            value = "{\"code\": 401, \"message\": \"Member not found 20202020\"}")}
            )),
            @ApiResponse(responseCode = "500", description = "서버 오류",
                    content = @Content(mediaType = "application/json"
                    ))
    })
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @interface LoginApiDoc {
    }
}
