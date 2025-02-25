package org.nova.backend.board.suggestion.adapter.web;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.nova.backend.board.suggestion.adapter.doc.SuggestionBoardApiDocument;
import org.nova.backend.board.suggestion.application.dto.request.SuggestionPostRequest;
import org.nova.backend.board.suggestion.application.dto.request.SuggestionReplyRequest;
import org.nova.backend.board.suggestion.application.dto.response.SuggestionPostDetailResponse;
import org.nova.backend.board.suggestion.application.dto.response.SuggestionPostSummaryResponse;
import org.nova.backend.board.suggestion.application.dto.response.SuggestionReplyResponse;
import org.nova.backend.board.suggestion.application.port.in.SuggestionPostUseCase;
import org.nova.backend.board.util.SecurityUtil;
import org.nova.backend.shared.model.ApiResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Suggestion Board API", description = "건의게시판 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/suggestions")
public class SuggestionBoardController {
    private final SuggestionPostUseCase suggestionPostUseCase;
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
        UUID memberId = securityUtil.getCurrentMemberIdOrNull();
        var posts = suggestionPostUseCase.getAllPosts(pageable, memberId);
        return ResponseEntity.ok(ApiResponse.success(posts));
    }

    @GetMapping("/{postId}")
    @SuggestionBoardApiDocument.GetPostById
    public ResponseEntity<ApiResponse<SuggestionPostDetailResponse>> getPostById(
            @PathVariable UUID postId
    ) {
        UUID memberId = securityUtil.getCurrentMemberIdOrNull();
        var post = suggestionPostUseCase.getPostById(postId, memberId);
        return ResponseEntity.ok(ApiResponse.success(post));
    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping("/{postId}/reply")
    @SuggestionBoardApiDocument.AddAdminReply
    public ResponseEntity<ApiResponse<SuggestionReplyResponse>> addAdminReply(
            @PathVariable UUID postId,
            @RequestBody SuggestionReplyRequest request
    ) {
        UUID adminId = securityUtil.getCurrentMemberId();
        var replyResponse = suggestionPostUseCase.addAdminReply(postId, request, adminId);
        return ResponseEntity.ok(ApiResponse.success(replyResponse));
    }

    @GetMapping("/search")
    @SuggestionBoardApiDocument.SearchPosts
    public ResponseEntity<ApiResponse<Page<SuggestionPostSummaryResponse>>> searchPostsByTitle(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false, defaultValue = "createdTime") String sortBy,
            @RequestParam(required = false, defaultValue = "desc") String sortDirection,
            @Parameter(hidden = true) Pageable pageable
    ) {
        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortBy);
        Pageable sortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);

        var posts = suggestionPostUseCase.searchPostsByTitle(keyword, sortedPageable);
        return ResponseEntity.ok(ApiResponse.success(posts));
    }
}
