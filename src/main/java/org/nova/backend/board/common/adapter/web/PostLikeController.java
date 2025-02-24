package org.nova.backend.board.common.adapter.web;

import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.nova.backend.board.common.adapter.doc.PostLikeApiDocument;
import org.nova.backend.board.common.application.port.in.BasePostUseCase;
import org.nova.backend.board.util.SecurityUtil;
import org.nova.backend.shared.model.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Post Like API", description = "게시글 좋아요 및 좋아요 취소 API")

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/posts/{postId}")
public class PostLikeController {
    private final BasePostUseCase postUseCase;
    private final SecurityUtil securityUtil;

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/like")
    @PostLikeApiDocument.LikePost
    public ResponseEntity<ApiResponse<Integer>> likePost(@PathVariable UUID postId) {
        UUID memberId = securityUtil.getCurrentMemberId();
        int likeCount = postUseCase.likePost(postId, memberId);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created(likeCount));
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/unlike")
    @PostLikeApiDocument.UnlikePost
    public ResponseEntity<ApiResponse<Integer>> unlikePost(@PathVariable UUID postId) {
        UUID memberId = securityUtil.getCurrentMemberId();
        int likeCount = postUseCase.unlikePost(postId, memberId);
        return ResponseEntity.ok(ApiResponse.success(likeCount));
    }
}
