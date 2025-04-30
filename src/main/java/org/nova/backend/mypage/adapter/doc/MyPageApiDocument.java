package org.nova.backend.mypage.adapter.doc;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Tag(name = "마이페이지 API", description = "마이페이지 전용 API")
public @interface MyPageApiDocument {
    @Operation(
            summary = "회원이 작성한 모든 게시글 조회",
            description = """
                로그인한 회원이 작성한 게시글 목록을 최신순으로 조회합니다.
                
                **정렬**
                - 정렬 기준: `createdTime` (고정)
                - 정렬 방식: `desc` (최신순, 고정)

                **예제 요청**
                `/api/v1/mypage/posts?page=0&size=10`
                """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "게시글 조회 성공", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "403", description = "인증 실패 (로그인이 필요합니다)"),
            @ApiResponse(responseCode = "500", description = "서버 에러")
    })
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @interface GetMyPosts {}
}
