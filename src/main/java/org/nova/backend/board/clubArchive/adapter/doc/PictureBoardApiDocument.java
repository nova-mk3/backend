package org.nova.backend.board.clubArchive.adapter.doc;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Tag(name = "Picture Board API", description = "사진 게시판 API")
public @interface PictureBoardApiDocument {

    @Operation(summary = "사진 게시글 생성", description = "새로운 사진 게시글을 생성합니다. \n\n **파일 업로드 후 `imageFileIds`를 포함하여 요청해야 합니다.**")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "게시글 생성 성공", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
            @ApiResponse(responseCode = "500", description = "서버 에러")
    })
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @interface CreatePost {}

    @Operation(summary = "사진 게시글 수정", description = "기존 사진 게시글을 수정합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "게시글 수정 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
            @ApiResponse(responseCode = "403", description = "수정 권한 없음"),
            @ApiResponse(responseCode = "404", description = "게시글을 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 에러")
    })
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @interface UpdatePost {}

    @Operation(summary = "사진 게시글 삭제", description = "게시글을 삭제합니다. (작성자 또는 관리자만 가능)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "게시글 삭제 성공"),
            @ApiResponse(responseCode = "403", description = "삭제 권한 없음"),
            @ApiResponse(responseCode = "404", description = "게시글을 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 에러")
    })
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @interface DeletePost {}

    @Operation(summary = "사진 게시글 조회", description = "사진 게시글을 ID를 기반으로 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "게시글 조회 성공"),
            @ApiResponse(responseCode = "404", description = "게시글을 찾을 수 없음")
    })
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @interface GetPostById {}
}