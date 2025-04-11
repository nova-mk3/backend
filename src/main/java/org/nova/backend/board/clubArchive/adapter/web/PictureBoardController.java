package org.nova.backend.board.clubArchive.adapter.web;

import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.nova.backend.board.clubArchive.adapter.doc.PictureBoardApiDocument;
import org.nova.backend.board.clubArchive.application.dto.request.PicturePostRequest;
import org.nova.backend.board.clubArchive.application.dto.request.UpdatePicturePostRequest;
import org.nova.backend.board.clubArchive.application.dto.response.PicturePostDetailResponse;
import org.nova.backend.board.clubArchive.application.port.in.PicturePostUseCase;
import org.nova.backend.board.util.SecurityUtil;
import org.nova.backend.shared.model.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Picture Board API", description = "사진 게시판 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/boards/{boardId}/picture-posts")
public class PictureBoardController {
    private final PicturePostUseCase picturePostUseCase;
    private final SecurityUtil securityUtil;

    /**
     * 사진 게시글 생성
     */
    @PreAuthorize("isAuthenticated()")
    @PostMapping
    @PictureBoardApiDocument.CreatePost
    public ResponseEntity<ApiResponse<PicturePostDetailResponse>> createPost(
            @PathVariable UUID boardId,
            @RequestBody PicturePostRequest request
    ) {
        UUID memberId = securityUtil.getCurrentMemberId();
        var savedPost = picturePostUseCase.createPost(boardId, request, memberId);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created(savedPost));
    }

    /**
     * 사진 게시글 수정
     */
    @PreAuthorize("isAuthenticated()")
    @PutMapping("/{postId}")
    @PictureBoardApiDocument.UpdatePost
    public ResponseEntity<ApiResponse<PicturePostDetailResponse>> updatePost(
            @PathVariable UUID boardId,
            @PathVariable UUID postId,
            @RequestBody UpdatePicturePostRequest request
    ) {
        UUID memberId = securityUtil.getCurrentMemberId();
        PicturePostDetailResponse updatedPost = picturePostUseCase.updatePost(boardId, postId, request, memberId);
        return ResponseEntity.ok(ApiResponse.success(updatedPost));
    }

    /**
     * 사진 게시글 삭제
     */
    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/{postId}")
    @PictureBoardApiDocument.DeletePost
    public ResponseEntity<ApiResponse<Void>> deletePost(
            @PathVariable UUID boardId,
            @PathVariable UUID postId
    ) {
        UUID memberId = securityUtil.getCurrentMemberId();
        picturePostUseCase.deletePost(boardId, postId, memberId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(ApiResponse.noContent());
    }

    /**
     * 특정 사진 게시글 조회
     */
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{postId}")
    @PictureBoardApiDocument.GetPostById
    public ResponseEntity<ApiResponse<PicturePostDetailResponse>> getPostById(
            @PathVariable UUID boardId,
            @PathVariable UUID postId
    ) {
        var post = picturePostUseCase.getPostById(boardId, postId);
        return ResponseEntity.ok(ApiResponse.success(post));
    }
}