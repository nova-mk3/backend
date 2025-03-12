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
            @ApiResponse(responseCode = "200", description = "새로운 회원 가입 요청 생성 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
            @ApiResponse(responseCode = "409", content = @Content(mediaType = "application/json",
                    examples = {@ExampleObject(name = "학번 or 이메일 중복",
                            value = "{\"code\": 409, \"message\": \"Member already exists. check student number or email 20202020 nova@chungbuk.ac.kr\"}")}
            )),
            @ApiResponse(responseCode = "500", description = "서버 오류"),
    })
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @interface SignUpApiDoc {
    }

    @Operation(summary = "회원가입시 프로필 사진 업로드", description = "회원가입 시 프로필 사진을 업로드합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "회원 프로필 사진 업로드 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
            @ApiResponse(responseCode = "500", description = "서버 오류"),
    })
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @interface UploadProfilePhotoApiDoc {
    }

    @Operation(summary = "로그인", description = "회원가입된 정보로 본인 인증 로그인")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "로그인 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
            @ApiResponse(responseCode = "401", description = "로그인 실패. 학번 또는 비밀번호를 다시 확인해주세요."),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @interface LoginApiDoc {
    }

    @Operation(summary = "로그아웃", description = "로그인한 정보를 삭제합니다. AUTH_TOKEN 담긴 쿠키 삭제")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "로그아웃 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
            @ApiResponse(responseCode = "403", description = "인증되지 않은 접근입니다."),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @interface LogoutApiDoc {
    }

    @Operation(summary = "회원 탈퇴", description = "Member의 isDeleted 필드를 true로 변경")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "회원 탈퇴 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
            @ApiResponse(responseCode = "403", description = "인증되지 않은 접근입니다."),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @interface WithdrawalApiDoc {
    }
}
