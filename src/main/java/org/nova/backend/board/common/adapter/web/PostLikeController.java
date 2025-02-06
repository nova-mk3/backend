package org.nova.backend.board.common.adapter.web;

import java.util.UUID;
import org.nova.backend.board.common.adapter.doc.PostLikeApiDocument;
import org.nova.backend.board.common.application.port.in.PostUseCase;
import org.nova.backend.member.adapter.repository.MemberRepository;
import org.nova.backend.member.domain.exception.MemberDomainException;
import org.nova.backend.member.domain.model.entity.Member;
import org.nova.backend.shared.model.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/posts/{postId}")
public class PostLikeController {
    private final PostUseCase postUseCase;
    private final MemberRepository memberRepository;

    public PostLikeController(
            PostUseCase postUseCase,
            MemberRepository memberRepository
    ) {
        this.postUseCase = postUseCase;
        this.memberRepository = memberRepository;
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/like")
    @PostLikeApiDocument.LikePost
    public ApiResponse<Integer> likePost(@PathVariable UUID postId) {
        UUID memberId = getCurrentMemberId();
        int likeCount = postUseCase.likePost(postId, memberId);
        return ApiResponse.success(likeCount);
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/unlike")
    @PostLikeApiDocument.UnlikePost
    public ApiResponse<Integer> unlikePost(@PathVariable UUID postId) {
        UUID memberId = getCurrentMemberId();
        int likeCount = postUseCase.unlikePost(postId, memberId);
        return ApiResponse.success(likeCount);
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
