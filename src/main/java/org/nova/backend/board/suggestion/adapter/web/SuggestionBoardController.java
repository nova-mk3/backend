package org.nova.backend.board.suggestion.adapter.web;

import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.nova.backend.board.suggestion.adapter.doc.SuggestionBoardApiDocument;
import org.nova.backend.board.suggestion.application.dto.request.SuggestionPostRequest;
import org.nova.backend.board.suggestion.application.dto.request.SuggestionReplyRequest;
import org.nova.backend.board.suggestion.application.dto.response.SuggestionPostDetailResponse;
import org.nova.backend.board.suggestion.application.dto.response.SuggestionPostSummaryResponse;
import org.nova.backend.board.suggestion.application.port.in.SuggestionPostUseCase;
import org.nova.backend.board.util.SecurityUtil;
import org.nova.backend.member.adapter.repository.MemberRepository;
import org.nova.backend.member.domain.model.entity.Member;
import org.nova.backend.shared.model.ApiResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Suggestion Board API", description = "건의게시판 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/suggestions")
public class SuggestionBoardController {
    private final SuggestionPostUseCase suggestionPostUseCase;
    private final MemberRepository memberRepository;
    private final SecurityUtil securityUtil;

    @PreAuthorize("isAuthenticated()")
    @PostMapping
    @SuggestionBoardApiDocument.CreatePost
    public ResponseEntity<ApiResponse<SuggestionPostDetailResponse>> createPost(
            @RequestBody SuggestionPostRequest request
    ) {
        UUID memberId = securityUtil.getCurrentMemberId();
        var savedPost = suggestionPostUseCase.createPost(request, memberId);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created(savedPost));
    }

    @GetMapping
    @SuggestionBoardApiDocument.GetAllPosts
    public ResponseEntity<ApiResponse<Page<SuggestionPostSummaryResponse>>> getAllPosts(
            Pageable pageable
    ) {
        UUID memberId = getCurrentMemberIdOrNull();
        var posts = suggestionPostUseCase.getAllPosts(pageable, memberId);
        return ResponseEntity.ok(ApiResponse.success(posts));
    }

    @GetMapping("/{postId}")
    @SuggestionBoardApiDocument.GetPostById
    public ResponseEntity<ApiResponse<SuggestionPostDetailResponse>> getPostById(
            @PathVariable UUID postId
    ) {
        UUID memberId = getCurrentMemberIdOrNull();
        var post = suggestionPostUseCase.getPostById(postId, memberId);
        return ResponseEntity.ok(ApiResponse.success(post));
    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping("/{postId}/reply")
    @SuggestionBoardApiDocument.AddAdminReply
    public ResponseEntity<ApiResponse<Void>> addAdminReply(
            @PathVariable UUID postId,
            @RequestBody SuggestionReplyRequest request
    ) {
        UUID adminId = securityUtil.getCurrentMemberId();
        suggestionPostUseCase.addAdminReply(postId, request, adminId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(ApiResponse.noContent());
    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping("/{postId}/read")
    @SuggestionBoardApiDocument.MarkAnswerAsRead
    public ResponseEntity<ApiResponse<Void>> markAnswerAsRead(
            @PathVariable UUID postId
    ) {
        UUID memberId = securityUtil.getCurrentMemberId();
        suggestionPostUseCase.markAnswerAsRead(postId, memberId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(ApiResponse.noContent());
    }

    /**
     * 현재 로그인한 사용자의 UUID 가져오기 (로그인 안 했을 경우 null 반환)
     */
    private UUID getCurrentMemberIdOrNull() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getName())) {
            return null;
        }

        String studentNumber = authentication.getName();
        return memberRepository.findByStudentNumber(studentNumber)
                .map(Member::getId)
                .orElse(null);
    }
}
