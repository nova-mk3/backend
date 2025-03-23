package org.nova.backend.member.adapter.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Tag(name = "관리자 API", description = "관리자가 회원 정보를 관리하기 위한 api입니다.")
public @interface AdminApiDocument {

    @Operation(summary = "전체 회원 학기 일괄 증가", description = "재학생들의 학기를 일괄 업데이트 시킵니다. 학기와 학년 정보가 자동으로 조정됩니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "전체 회원 학기 일괄 업데이트 완료"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
            @ApiResponse(responseCode = "401", description = "로그인이 필요합니다."),
            @ApiResponse(responseCode = "403", description = "인증되지 않은 접근입니다."),
            @ApiResponse(responseCode = "404", description = "업데이트 할 멤버가 없습니다"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @interface UpdateAllGradeApiDoc {
    }

    @Operation(summary = "특정 회원의 휴학 여부 변경", description = "요청받은 회원의 휴학 여부를 변경합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "휴학 여부 변경 완료"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
            @ApiResponse(responseCode = "401", description = "로그인이 필요합니다."),
            @ApiResponse(responseCode = "403", description = "인증되지 않은 접근입니다."),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없습니다."),
            @ApiResponse(responseCode = "409", description = "졸업생은 휴학 여부를 변경할 수 없습니다."),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @interface UpdateAbsenceApiDoc {
    }

    @Operation(summary = "특정 회원의 졸업 여부 변경", description = "요청받은 회원의 졸업 여부를 변경합니다. 기존에 졸업 정보가 없는 경우 빈 값이 들어갑니다. 휴학 여부는 false로 변경합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "졸업 여부 변경 완료"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
            @ApiResponse(responseCode = "401", description = "로그인이 필요합니다."),
            @ApiResponse(responseCode = "403", description = "인증되지 않은 접근입니다."),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없습니다."),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @interface UpdateGraduationApiDoc {
    }

    @Operation(summary = "특정 회원 학년 변경", description = "특정 회원의 학년 정보를 변경합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "회원 학년 정보 변경 완료"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
            @ApiResponse(responseCode = "401", description = "로그인이 필요합니다."),
            @ApiResponse(responseCode = "403", description = "인증되지 않은 접근입니다."),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없습니다."),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @interface UpdateGradeApiDoc {
    }

    @Operation(summary = "모든 회원 목록 조회", description = "모든 회원의 목록을 불러옵니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "회원 목록 조회 완료"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
            @ApiResponse(responseCode = "401", description = "로그인이 필요합니다."),
            @ApiResponse(responseCode = "403", description = "인증되지 않은 접근입니다."),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없습니다."),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @interface GetAllMembersApiDoc {
    }

    @Operation(summary = "회원 이름으로 검색", description = "회원 목록에서 이름으로 검색합니다.")
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
    @interface FindMembersByNameApiDoc {
    }

    @Operation(summary = "회원 정보 단건 변경", description = "관리자 권한으로 특정 회원의 정보를 변경합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "회원 정보 변경 완료"),
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

    @Operation(summary = "회원 탈퇴", description = "관리자 권한으로 회원을 탈퇴시킵니다. isDeleted=true로 변경합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "회원 탈퇴 완료"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
            @ApiResponse(responseCode = "401", description = "로그인이 필요합니다."),
            @ApiResponse(responseCode = "403", description = "인증되지 않은 접근입니다."),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @interface DeleteMemberApiDoc {
    }

}
