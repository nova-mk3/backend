package org.nova.backend.board.adapter.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.UUID;
import org.nova.backend.board.application.dto.request.CommentRequest;
import org.nova.backend.board.application.dto.response.CommentResponse;
import org.nova.backend.board.application.port.in.CommentUseCase;
import org.nova.backend.member.adapter.repository.MemberRepository;
import org.nova.backend.member.domain.model.entity.Member;
import org.nova.backend.shared.model.ApiResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Comment API", description = "모든 게시글 댓글 API")
@RestController
@RequestMapping("/api/v1/comments")
public class CommentController {
    private final CommentUseCase commentUseCase;
    private final MemberRepository memberRepository;

    public CommentController(
            CommentUseCase commentUseCase,
            MemberRepository memberRepository
    ){
        this.commentUseCase = commentUseCase;
        this.memberRepository = memberRepository;
    }

    @Operation(summary = "댓글 작성")
    @PostMapping
    public ApiResponse<CommentResponse> createComment(@RequestBody CommentRequest request) {
        Member member = getCurrentMember();
        return ApiResponse.success(commentUseCase.addComment(request, member));
    }

    @Operation(summary = "게시글의 모든 댓글 조회")
    @GetMapping("/{postId}")
    public ApiResponse<List<CommentResponse>> getCommentsByPostId(@PathVariable UUID postId) {
        return ApiResponse.success(commentUseCase.getCommentsByPostId(postId));
    }

    private Member getCurrentMember() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return memberRepository.findByStudentNumber(authentication.getName())
                .orElseThrow(() -> new IllegalStateException("사용자를 찾을 수 없습니다."));
    }
}
