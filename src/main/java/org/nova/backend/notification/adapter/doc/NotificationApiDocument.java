package org.nova.backend.notification.adapter.doc;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

public @interface NotificationApiDocument {

    @Operation(summary = "사용자 알림 목록 조회", description = "현재 로그인한 사용자의 알림을 페이징 형태로 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "알림 조회 성공")
    })
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @interface GetNotifications {}

    @Operation(summary = "읽지 않은 알림 개수 조회", description = "현재 사용자의 안 읽은 알림 수를 반환합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "개수 반환 성공")
    })
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @interface CountUnread {}

    @Operation(summary = "알림 읽음 처리", description = "알림을 읽음 처리합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "읽음 처리 성공"),
            @ApiResponse(responseCode = "403", description = "읽을 권한 없음"),
            @ApiResponse(responseCode = "404", description = "알림 없음")
    })
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @interface MarkAsRead {}
}
