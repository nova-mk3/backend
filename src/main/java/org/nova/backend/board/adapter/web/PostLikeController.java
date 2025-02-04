package org.nova.backend.board.adapter.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.UUID;
import org.nova.backend.board.application.port.in.PostUseCase;
import org.nova.backend.member.adapter.repository.MemberRepository;
import org.nova.backend.member.domain.exception.MemberDomainException;
import org.nova.backend.member.domain.model.entity.Member;
import org.nova.backend.shared.model.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Post Like API", description = "게시글 좋아요 및 좋아요 취소 API")
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

    @Operation(summary = "게시글 좋아요 추가", description = "특정 게시글에 좋아요를 추가합니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "좋아요 추가 성공", content = @Content(mediaType = "application/json")),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증되지 않은 사용자", content = @Content(mediaType = "application/json")),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "게시글을 찾을 수 없음", content = @Content(mediaType = "application/json"))
    })
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/like")
    public ApiResponse<Integer> likePost(@PathVariable UUID postId) {
        Member member = getCurrentMember();
        int likeCount = postUseCase.likePost(postId, member);
        return ApiResponse.success(likeCount);
    }

    @Operation(summary = "게시글 좋아요 취소", description = "특정 게시글의 좋아요를 취소합니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "좋아요 취소 성공", content = @Content(mediaType = "application/json")),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증되지 않은 사용자", content = @Content(mediaType = "application/json")),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "게시글을 찾을 수 없음", content = @Content(mediaType = "application/json"))
    })
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/unlike")
    public ApiResponse<Integer> unlikePost(@PathVariable UUID postId) {
        Member member = getCurrentMember();
        int likeCount = postUseCase.unlikePost(postId, member);
        return ApiResponse.success(likeCount);
    }

    private Member getCurrentMember() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return memberRepository.findByStudentNumber(authentication.getName())
                .orElseThrow(() -> new MemberDomainException("사용자를 찾을 수 없습니다.", HttpStatus.NOT_FOUND));
    }
}
