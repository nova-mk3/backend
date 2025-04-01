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

@Tag(name = "임원 관리 API", description = "관리자가 임원을 관리합니다.")
public @interface ExecutiveHistoryApiDocument {

    @Operation(summary = "모든 연도 조회", description = "연도 리스트를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "리스트 조회 성공"),
            @ApiResponse(responseCode = "403", description = "인증되지 않은 접근입니다."),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @interface GetYearListApiDoc {
    }

    @Operation(summary = "연도 추가", description = "현재 연도 리스트에서 가장 최근 연도를 찾아 +1 한 값을 추가합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "연도 추가 성공"),
            @ApiResponse(responseCode = "403", description = "인증되지 않은 접근입니다."),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @interface AddYearApiDoc {
    }

    @Operation(summary = "연도 삭제", description = "현재 연도 리스트에서 가장 최근 연도를 찾아 삭제합니다. 삭제된 연도의 임원 이력을 삭제하고 최신 연도 임원들에게 권한을 줍니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "연도 삭제 성공"),
            @ApiResponse(responseCode = "403", description = "인증되지 않은 접근입니다."),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @interface DeleteYearApiDoc {
    }

    @Operation(summary = "임원 권한 변경", description = "임원에게 노바 홈페이지 관리 권한을 부여합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "임원 권한 변경 성공"),
            @ApiResponse(responseCode = "403", description = "인증되지 않은 접근입니다."),
            @ApiResponse(responseCode = "404", description = "해당 임원을 찾을 수 없습니다."),
            @ApiResponse(responseCode = "409", description = "해당 임원에게 권한을 부여할 수 없습니다."),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @interface UpdateExecutivesRoleApiDoc {
    }

    @Operation(summary = "특정 연도의 임원 이력 조회", description = "특정 연도의 임원 이력을 반환합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "임원 이력 조회 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
            @ApiResponse(responseCode = "403", description = "인증되지 않은 접근입니다."),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @interface GetExecutiveHistoryByYearApiDoc {
    }

    @Operation(summary = "임원 이력 추가", description = "요청에 대한 새로운 임원 이력 추가")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "임원 이력 추가 완료"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
            @ApiResponse(responseCode = "403", description = "인증되지 않은 접근입니다."),
            @ApiResponse(responseCode = "409", description = "연도 추가 후 임원 이력을 추가해주세요."),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @interface AddExecutiveHistoryApiDoc {
    }

    @Operation(summary = "임원 이력 삭제", description = "요청에 대한 임원 이력 삭제")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "임원 이력 삭제 완료"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
            @ApiResponse(responseCode = "403", description = "인증되지 않은 접근입니다."),
            @ApiResponse(responseCode = "404", content = @Content(mediaType = "application/json",
                    examples = {@ExampleObject(name = "ExecutiveHistory 조회 실패",
                            value = "{\"code\": 404, \"message\": \"executive history not found. 20202020\"}"),
                    }
            )),
            @ApiResponse(responseCode = "500", description = "서버 오류",
                    content = @Content(mediaType = "application/json"))
    })
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @interface DeleteExecutiveHistoryApiDoc {
    }

    @Operation(summary = "모든 회원 목록 불러오기", description = "임원 추가를 위해 회원 목록을 불러옵니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "모든 회원 목록 반환 완료"),
            @ApiResponse(responseCode = "403", description = "인증되지 않은 접근입니다."),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @interface GetAllMembersApiDoc {
    }

    @Operation(summary = "회원 정보 조회", description = "요청한 사용자의 상세 정보를 조회합니다.")
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
}
