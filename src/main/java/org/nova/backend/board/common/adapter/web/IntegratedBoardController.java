package org.nova.backend.board.common.adapter.web;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.nova.backend.board.common.adapter.doc.IntegratedBoardApiDocument;
import org.nova.backend.board.common.application.dto.request.BasePostRequest;
import org.nova.backend.board.common.application.dto.request.UpdateBasePostRequest;
import org.nova.backend.board.common.application.dto.response.BasePostDetailResponse;
import org.nova.backend.board.common.application.dto.response.BasePostSummaryResponse;
import org.nova.backend.board.common.application.port.in.BasePostUseCase;
import org.nova.backend.board.common.domain.model.valueobject.PostType;
import org.nova.backend.board.util.SecurityUtil;
import org.nova.backend.shared.model.ApiResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Integrated Post API", description = "통합 게시판 공통 API (QnA, 자유게시판, 자기소개, 공지사항)")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/boards/{boardId}/posts")
public class IntegratedBoardController {
    private final BasePostUseCase basePostUseCase;
    private final SecurityUtil securityUtil;


    @PreAuthorize("isAuthenticated()")
    @PostMapping
    @IntegratedBoardApiDocument.CreatePost
    public ResponseEntity<ApiResponse<BasePostDetailResponse>> createPost(
            @PathVariable UUID boardId,
            @RequestBody BasePostRequest request
    ) {
        UUID memberId = securityUtil.getCurrentMemberId();
        var savedPost = basePostUseCase.createPost(boardId, request, memberId);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created(savedPost));
    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping(value = "/{postId}")
    @IntegratedBoardApiDocument.UpdatePost
    public ResponseEntity<ApiResponse<BasePostDetailResponse>> updatePost(
            @PathVariable UUID boardId,
            @PathVariable UUID postId,
            @RequestBody UpdateBasePostRequest request
    ) {
        UUID memberId = securityUtil.getCurrentMemberId();
        BasePostDetailResponse updatedPost = basePostUseCase.updatePost(boardId, postId, request, memberId);
        return ResponseEntity.ok(ApiResponse.success(updatedPost));
    }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/{postId}")
    @IntegratedBoardApiDocument.DeletePost
    public ResponseEntity<ApiResponse<Void>> deletePost(
            @PathVariable UUID boardId,
            @PathVariable UUID postId
    ) {
        UUID memberId = securityUtil.getCurrentMemberId();
        basePostUseCase.deletePost(boardId, postId, memberId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(ApiResponse.noContent());
    }

    @GetMapping
    @IntegratedBoardApiDocument.GetPostsByCategory
    public ResponseEntity<ApiResponse<Page<?>>> getPostsByCategory(
            @PathVariable UUID boardId,
            @RequestParam PostType postType,
            @RequestParam(required = false, defaultValue = "createdTime") String sortBy,
            @RequestParam(required = false, defaultValue = "desc") String sortDirection,
            @Parameter(hidden = true) Pageable pageable
    ) {
        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortBy);
        Pageable sortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);

        var posts = basePostUseCase.getPostsByCategory(boardId, postType, sortedPageable);
        return ResponseEntity.ok(ApiResponse.success(posts));
    }

    @GetMapping("/search")
    @IntegratedBoardApiDocument.SearchPostsByCategory
    public ResponseEntity<ApiResponse<Page<?>>> searchPostsByCategory(
            @PathVariable UUID boardId,
            @RequestParam PostType postType,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false, defaultValue = "ALL") String searchType,
            @RequestParam(required = false, defaultValue = "createdTime") String sortBy,
            @RequestParam(required = false, defaultValue = "desc") String sortDirection,
            @Parameter(hidden = true) Pageable pageable
    ) {
        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortBy);
        Pageable sortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);

        var posts = basePostUseCase.searchPostsByCategory(boardId, postType, keyword, searchType, sortedPageable);
        return ResponseEntity.ok(ApiResponse.success(posts));
    }

    @GetMapping("/{postId}")
    @IntegratedBoardApiDocument.GetPostById
    public ResponseEntity<ApiResponse<BasePostDetailResponse>> getPostById(
            @PathVariable UUID boardId,
            @PathVariable UUID postId
    ) {
        var post = basePostUseCase.getPostById(boardId, postId);
        return ResponseEntity.ok(ApiResponse.success(post));
    }

    @GetMapping("/latest")
    @IntegratedBoardApiDocument.GetLatestPostByType
    public ResponseEntity<ApiResponse<Map<PostType, List<BasePostSummaryResponse>>>> getLatestPostsByType(
            @PathVariable UUID boardId
    ) {
        var latestPosts = basePostUseCase.getLatestPostsByType(boardId);
        return ResponseEntity.ok(ApiResponse.success(latestPosts));
    }

    @GetMapping("/all")
    @IntegratedBoardApiDocument.GetAllPosts
    public ResponseEntity<ApiResponse<Page<BasePostSummaryResponse>>> getAllPosts(
            @PathVariable UUID boardId,
            @RequestParam(required = false, defaultValue = "createdTime") String sortBy,
            @RequestParam(required = false, defaultValue = "desc") String sortDirection,
            Pageable pageable
    ) {
        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortBy);
        Pageable sortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);

        var posts = basePostUseCase.getAllPosts(boardId, sortedPageable);
        return ResponseEntity.ok(ApiResponse.success(posts));
    }
}