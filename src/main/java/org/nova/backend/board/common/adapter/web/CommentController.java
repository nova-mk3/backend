package org.nova.backend.board.common.adapter.web;

import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.UUID;
import org.nova.backend.board.common.adapter.doc.CommentApiDocument;
import org.nova.backend.board.common.application.dto.request.CommentRequest;
import org.nova.backend.board.common.application.dto.request.UpdateCommentRequest;
import org.nova.backend.board.common.application.dto.response.CommentResponse;
import org.nova.backend.board.common.application.port.in.CommentUseCase;
import org.nova.backend.member.adapter.repository.MemberRepository;
import org.nova.backend.member.domain.exception.MemberDomainException;
import org.nova.backend.member.domain.model.entity.Member;
import org.nova.backend.shared.model.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Comment API", description = "모든 게시글 댓글 API")
@RestController
@RequestMapping("/api/v1")
public class CommentController {
    private final CommentUseCase commentUseCase;
    private final MemberRepository memberRepository;

    public CommentController(
            CommentUseCase commentUseCase,
            MemberRepository memberRepository
    ) {
        this.commentUseCase = commentUseCase;
        this.memberRepository = memberRepository;
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/posts/{postId}/comments")
    @CommentApiDocument.CreateComment
    public ResponseEntity<ApiResponse<CommentResponse>> createComment(
            @PathVariable UUID postId,
            @RequestBody CommentRequest request
    ) {
        UUID memberId = getCurrentMemberId();
        CommentResponse createdComment = commentUseCase.addComment(postId, request, memberId);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created(createdComment));
    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping("/comments/{commentId}")
    @CommentApiDocument.UpdateComment
    public ResponseEntity<ApiResponse<CommentResponse>> updateComment(
            @PathVariable UUID commentId,
            @RequestBody UpdateCommentRequest request
    ) {
        UUID memberId = getCurrentMemberId();
        CommentResponse updatedComment = commentUseCase.updateComment(commentId, request, memberId);
        return ResponseEntity.ok(ApiResponse.success(updatedComment));
    }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/comments/{commentId}")
    @CommentApiDocument.DeleteComment
    public ResponseEntity<ApiResponse<Void>> deleteComment(
            @PathVariable UUID commentId
    ) {
        UUID memberId = getCurrentMemberId();
        commentUseCase.deleteComment(commentId, memberId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(ApiResponse.noContent());
    }

    @GetMapping("/posts/{postId}/comments")
    @CommentApiDocument.GetCommentsByPost
    public ResponseEntity<ApiResponse<List<CommentResponse>>> getCommentsByPostId(@PathVariable UUID postId) {
        List<CommentResponse> comments = commentUseCase.getCommentsByPostId(postId);
        return ResponseEntity.ok(ApiResponse.success(comments));
    }

    /**
     * 현재 로그인한 사용자의 UUID 가져오기
     */
    private UUID getCurrentMemberId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String studentNumber = authentication.getName();

        return memberRepository.findByStudentNumber(studentNumber)
                .map(Member::getId)
                .orElseThrow(() -> new MemberDomainException("사용자를 찾을 수 없습니다.", HttpStatus.NOT_FOUND));
    }
}
