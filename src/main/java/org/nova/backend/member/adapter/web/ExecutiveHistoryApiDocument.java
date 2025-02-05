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
            @ApiResponse(responseCode = "200", description = "리스트 조회 성공",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "500", description = "서버 오류",
                    content = @Content(mediaType = "application/json"))
    })
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @interface GetYearListApiDoc {
    }

    @Operation(summary = "특정 연도의 임원 이력 조회", description = "특정 연도의 임원 이력을 반환합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "임원 이력 조회 성공",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "500", description = "서버 오류",
                    content = @Content(mediaType = "application/json"))
    })
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @interface GetExecutiveHistoryByYearApiDoc {
    }

    @Operation(summary = "임원 추가", description = "요청에 대한 새로운 임원 이력 추가")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "임원 이력 추가 완료",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "500", description = "서버 오류",
                    content = @Content(mediaType = "application/json"))
    })
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @interface AddExecutiveHistoryApiDoc {
    }

    @Operation(summary = "임원 삭제", description = "요청에 대한 임원 이력 삭제")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "임원 이력 삭제 완료",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터",
                    content = @Content(mediaType = "application/json")),
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
            @ApiResponse(responseCode = "200", description = "모든 회원 목록 반환 완료",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "500", description = "서버 오류",
                    content = @Content(mediaType = "application/json"))
    })
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @interface GetAllMembersApiDoc {
    }
}
