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
                            value = "{\"code\": 409, \"message\": \"Member already exists. check student number or email 20202020 nova@chungbuk.ac.kr\"}"),
                            @ExampleObject(name = "졸업생은 휴학중일 수 없습니다.",
                                    value = "{\"code\": 409, \"message\": \"졸업생은 휴학중일 수 없습니다.\"}")}
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

    @Operation(summary = "access token 만료 여부 확인", description = "사용자의 Access token 만료 여부를 반환합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "만료되지 않은 Access token"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
            @ApiResponse(responseCode = "401", description = "만료된 Access token"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @interface VerifyAccessTokenApiDoc {
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

    @Operation(
            summary = "비밀번호 초기화",
            description = """
        사용자가 이름과 이메일을 입력하여 임시 비밀번호를 요청하면, 등록된 회원 정보와 일치하는 경우 
        해당 이메일 주소로 임시 비밀번호를 발급하여 전송합니다.
        
        ✔️ 발급된 임시 비밀번호는 10자리 랜덤 문자열이며, 기존 비밀번호를 대체하여 저장됩니다.
        ✔️ 사용자의 비밀번호는 암호화되어 저장되며, `isTempPassword` 플래그가 true로 설정됩니다.
        ✔️ 로그인 시 `isTempPassword`가 true인 사용자는 비밀번호 변경 페이지로 리다이렉트해야 합니다.        
        """,
            tags = {"Password Reset API"}
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "임시 비밀번호 이메일 전송 성공"),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @interface PasswordResetApiDoc {}
}
