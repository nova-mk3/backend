package org.nova.backend.board.common.adapter.doc;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.ElementType;

public @interface AllPostsApiDocument {

    @Operation(
            summary = "EXAM_ARCHIVE를 제외한 모든 게시글 조회",
            description = """
                    전체 게시판에서 EXAM_ARCHIVE(족보 게시판)을 제외한 게시글을 페이징하여 조회합니다.

                    **정렬 기준 (`sortBy`)**
                    - `createdTime` (기본값) → 생성일 기준 정렬
                    - `modifiedTime` → 수정일 기준 정렬
                    - `viewCount` → 조회수 기준 정렬

                    **정렬 방식 (`sortDirection`)**
                    - `desc` (기본값) → 내림차순 (최신, 높은 값 먼저)
                    - `asc` → 오름차순 (오래된, 낮은 값 먼저)

                    **예제**
                    `/api/v1/posts/across-boards?sortBy=createdTime&sortDirection=desc&page=0&size=10`
                    """
    )
    @ApiResponse(responseCode = "200", description = "게시글 목록 조회 성공", content = @Content(mediaType = "application/json"))
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @interface GetAllPostsAcrossBoards {}
}