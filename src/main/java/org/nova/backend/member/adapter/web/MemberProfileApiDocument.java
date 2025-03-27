package org.nova.backend.member.adapter.web;

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

@Tag(name = "마이페이지 API", description = "마이페이지 회원 정보 관리 api 입니다.")
public @interface MemberProfileApiDocument {

    @Operation(summary = "회원 pk 조회", description = "현재 로그인한 사용자의 pk를 조회합니다. 로그인이 되어있지 않은 상태이면 null을 반환합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "회원 pk 조회 성공"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @interface GetMemberPKApiDoc {
    }

    @Operation(summary = "회원 간단 프로필 조회", description = "현재 로그인한 사용자의 pk, 이름, 프로필 사진을 반환합니다. 로그인이 되어있지 않은 상태이면 null을 반환합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "회원 간단 프로필 조회 성공"),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없습니다."),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @interface GetMemberSimpleProfileApiDoc {
    }

    @Operation(summary = "회원 정보 조회", description = "사용자의 정보를 조회합니다. isLoginMember에 해당 프로필이 로그인한 사용자의 정보인지 담아서 반환합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "회원 정보 조회 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
            @ApiResponse(responseCode = "401", description = "로그인이 필요합니다."),
            @ApiResponse(responseCode = "403", description = "인증되지 않은 접근입니다."),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없습니다."),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @interface GetMemberProfileApiDoc {
    }

    @Operation(summary = "회원 정보 수정", description = "사용자의 정보를 수정합니다. isLoginMember에 해당 프로필이 로그인한 사용자의 정보인지 담아서 반환합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "회원 정보 수정 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
            @ApiResponse(responseCode = "401", description = "로그인이 필요합니다."),
            @ApiResponse(responseCode = "403", description = "인증되지 않은 접근입니다."),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없습니다."),
            @ApiResponse(responseCode = "409", description = "졸업생은 휴학중일 수 없습니다."),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @interface UpdateMemberProfileApiDoc {
    }

    @Operation(summary = "회원 비밀번호 변경", description = "새 비밀번호로 비밀번호를 바꿉니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "비밀번호 변경 성공"),
            @ApiResponse(responseCode = "400", description = "새 비밀번호와 비밀번호 확인이 일치하지 않습니다."),
            @ApiResponse(responseCode = "401", description = "로그인이 필요합니다."),
            @ApiResponse(responseCode = "403", description = "인증되지 않은 접근입니다."),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없습니다."),
            @ApiResponse(responseCode = "409", description = "비밀번호 확인 실패"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @interface UpdatePasswordApiDoc {
    }

    @Operation(summary = "회원 이메일 변경을 위한 인증 코드 전송", description = "회원 이메일로 인증 코드를 전송합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "이메일 전송 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
            @ApiResponse(responseCode = "401", description = "로그인이 필요합니다."),
            @ApiResponse(responseCode = "403", description = "인증되지 않은 접근입니다."),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없습니다."),
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
    @interface SendEmailAuthCode {
    }

    @Operation(summary = "이메일 인증 코드 확인", description = "이메일 확인을 위한 인증 코드를 확인합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "인증 코드 확인 완료"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
            @ApiResponse(responseCode = "401", description = "로그인이 필요합니다."),
            @ApiResponse(responseCode = "403", description = "인증되지 않은 접근입니다."),
            @ApiResponse(responseCode = "409", description = "인증 코드 확인 실패"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @interface CheckEmailAuthCode {
    }

    @Operation(summary = "회원 이메일 변경", description = "이메일 인증 후 회원의 이메일 정보를 변경합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "이메일 변경 완료"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
            @ApiResponse(responseCode = "401", description = "로그인이 필요합니다."),
            @ApiResponse(responseCode = "403", description = "인증되지 않은 접근입니다."),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없습니다."),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @interface UpdateEmail {
    }

    @Operation(summary = "회원 이메일 조회", description = "회원의 이메일 정보를 반환합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "이메일 조회 완료"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
            @ApiResponse(responseCode = "401", description = "로그인이 필요합니다."),
            @ApiResponse(responseCode = "403", description = "인증되지 않은 접근입니다."),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없습니다."),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @interface GetEmail {
    }

    @Operation(summary = "회원 프로필 사진 업로드", description = "프로필 사진을 DB에 업로드합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "회원 프로필 사진 업로드 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
            @ApiResponse(responseCode = "401", description = "로그인이 필요합니다."),
            @ApiResponse(responseCode = "403", description = "인증되지 않은 접근입니다."),
            @ApiResponse(responseCode = "500", description = "서버 오류"),
    })
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @interface UploadProfilePhoto {
    }

    @Operation(summary = "회원 프로필 사진 변경", description = "회원의 프로필 사진을 변경합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "회원 프로필 사진 변경 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
            @ApiResponse(responseCode = "401", description = "로그인이 필요합니다."),
            @ApiResponse(responseCode = "403", description = "인증되지 않은 접근입니다."),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없습니다."),
            @ApiResponse(responseCode = "404", description = "프로필 사진을 찾을 수 없습니다."),
            @ApiResponse(responseCode = "500", description = "서버 오류"),
    })
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @interface UpdateProfilePhoto {
    }

    @Operation(summary = "회원 프로필 사진 삭제", description = "회원의 프로필 사진을 삭제합니다. 기본 이미지를 반환합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "회원 프로필 사진 삭제 성공. 기본 이미지를 반환합니다."),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
            @ApiResponse(responseCode = "401", description = "로그인이 필요합니다."),
            @ApiResponse(responseCode = "403", description = "인증되지 않은 접근입니다."),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없습니다."),
            @ApiResponse(responseCode = "404", description = "기본 프로필 사진을 찾을 수 없습니다."),
            @ApiResponse(responseCode = "500", description = "서버 오류"),
    })
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @interface DeleteProfilePhoto {
    }

    @Operation(summary = "회원 프로필 사진 다운로드", description = "회원의 프로필 사진을 다운로드합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "회원 프로필 사진 다운로드 성공."),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
            @ApiResponse(responseCode = "401", description = "로그인이 필요합니다."),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없습니다."),
            @ApiResponse(responseCode = "404", description = "프로필 사진을 찾을 수 없습니다."),
            @ApiResponse(responseCode = "500", description = "서버 오류"),
    })
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @interface DownloadProfilePhoto {
    }

}
