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

@Tag(name = "회원가입 요청 처리 API", description = "관리자가 회원가입 요청을 처리합니다.")
public @interface PendingMemberApiDocument {

    @Operation(summary = "모든 회원가입 요청 리스트 조회", description = "모든 PendingMember 리스트를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "리스트 조회 성공",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "500", description = "서버 오류",
                    content = @Content(mediaType = "application/json"))
    })
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @interface GetPendingMemberListApiDoc {
    }

    @Operation(summary = "특정 PendingMember의 상세 정보 조회", description = "요청의 PendingMember와 PendingGraduation 를 반환합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "상세 정보 조회 성공",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", content = @Content(mediaType = "application/json",
                    examples = {@ExampleObject(name = "PendingMember 조회 실패",
                            value = "{\"code\": 404, \"message\": \"pending member not found. 20202020\"}"),
                    }
            )),
            @ApiResponse(responseCode = "500", description = "서버 오류",
                    content = @Content(mediaType = "application/json"))
    })
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @interface GetPendingMemberDetailApiDoc {
    }

    @Operation(summary = "회원가입 요청 단건 수락", description = "요청의 PendingMember, PendingGraduation로 Member, Graduation를 생성합니다.  ")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "회원가입 요청 수락 완료",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", content = @Content(mediaType = "application/json",
                    examples = {@ExampleObject(name = "PendingMember 조회 실패",
                            value = "{\"code\": 404, \"message\": \"pending member not found. 20202020\"}"),
                    }
            )),
            @ApiResponse(responseCode = "500", description = "서버 오류",
                    content = @Content(mediaType = "application/json"))
    })
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @interface AcceptPendingMemberApiDoc {
    }

    @Operation(summary = "회원가입 요청 단건 반려", description = "요청의 PendingMember의 isReject를 true로 변경합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "회원가입 요청 반려 완료",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", content = @Content(mediaType = "application/json",
                    examples = {@ExampleObject(name = "PendingMember 조회 실패",
                            value = "{\"code\": 404, \"message\": \"pending member not found. 20202020\"}"),
                    }
            )),
            @ApiResponse(responseCode = "500", description = "서버 오류",
                    content = @Content(mediaType = "application/json"))
    })
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @interface RejectPendingMemberApiDoc {
    }
}
