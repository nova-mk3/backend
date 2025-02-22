package org.nova.backend.board.common.adapter.doc;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Tag(name = "Integrated Post API", description = "통합 게시판 공통 API (QnA, 자유게시판, 자기소개, 공지사항)")
public @interface IntegratedBoardApiDocument {

    @Operation(summary = "게시글 생성", description = "새로운 게시글을 생성합니다. \n\n **파일 업로드 후 `fileIds`를 포함하여 요청해야 합니다.**")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "게시글 생성 성공", content = @Content(mediaType = "application/json")),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청 데이터", content = @Content(mediaType = "application/json")),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "서버 에러", content = @Content(mediaType = "application/json"))
    })
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @interface CreatePost {}

    @Operation(summary = "게시글 수정", description = "기존 게시글을 수정합니다. \n\n **파일 수정 시 `fileIds`를 포함하여 요청해야 합니다.**")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "204", description = "게시글 수정 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "수정 권한 없음"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "게시글을 찾을 수 없음"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "서버 에러")
    })
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @interface UpdatePost {}

    @Operation(summary = "게시글 삭제", description = "게시글을 삭제합니다. (작성자 또는 관리자만 가능)")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "204", description = "게시글 삭제 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "삭제 권한 없음"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "게시글을 찾을 수 없음"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "서버 에러")
    })
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @interface DeletePost {}

    @Operation(summary = "게시글 조회", description = "게시글을 ID를 기반으로 조회합니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "게시글 조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "게시글을 찾을 수 없음")
    })
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @interface GetPostById {}

    @Operation(
            summary = "카테고리별 게시글 조회",
            description = """
                    특정 게시판 카테고리에 속한 게시글 목록을 가져옵니다.\s
                    
                    **정렬 기준:**
                    - `createdTime` (생성일 기준 정렬)
                    - `modifiedTime` (수정일 기준 정렬)
                    - `viewCount` (조회수 기준 정렬)
                    
                    **정렬 방식:**
                    - `desc` (내림차순, 높은 값부터)
                    - `asc` (오름차순, 낮은 값부터)"""
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "게시글 목록 조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청 데이터")
    })
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @interface GetPostsByCategory {}

    @Operation(summary = "각 PostType별 최신 게시글 조회", description = "각 PostType(QnA, 자유게시판, 자기소개, 공지사항)별 최신 6개 게시글을 가져옵니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "게시글 조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청 데이터")
    })
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @interface GetLatestPostByType {}

    @Operation(
            summary = "모든 게시글 조회",
            description = """
                    카테고리 구분 없이 특정 게시판 내의 모든 게시글을 페이징하여 조회합니다.\s
                    
                    **정렬 기준:**
                    - `createdTime` (생성일 기준 정렬)
                    - `modifiedTime` (수정일 기준 정렬)
                    - `viewCount` (조회수 기준 정렬)
                    
                    **정렬 방식:**
                    - `desc` (내림차순, 높은 값부터)
                    - `asc` (오름차순, 낮은 값부터)"""
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "게시글 목록 조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청 데이터")
    })
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @interface GetAllPosts {}

    @Operation(
            summary = "카테고리별 게시글 검색",
            description = """
                    특정 게시판 카테고리에서 키워드를 기반으로 게시글을 검색합니다.
                    
                    **검색 기준:**
                    - `TITLE` (제목에서 검색)
                    - `CONTENT` (내용에서 검색)
                    - `ALL` (제목 + 내용에서 검색)
                    
                    **정렬 기준:**
                    - `createdTime` (생성일 기준 정렬)
                    - `modifiedTime` (수정일 기준 정렬)
                    - `viewCount` (조회수 기준 정렬)
                    
                    **정렬 방식:**
                    - `desc` (내림차순, 높은 값부터)
                    - `asc` (오름차순, 낮은 값부터)"""
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "게시글 검색 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청 데이터")
    })
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @interface SearchPostsByCategory {}
}