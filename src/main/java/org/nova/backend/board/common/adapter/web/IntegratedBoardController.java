package org.nova.backend.board.common.adapter.web;

import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.nova.backend.board.common.adapter.doc.IntegratedBoardApiDocument;
import org.nova.backend.board.common.application.dto.request.BasePostRequest;
import org.nova.backend.board.common.application.dto.request.UpdatePostRequest;
import org.nova.backend.board.common.application.dto.response.PostDetailResponse;
import org.nova.backend.board.common.application.dto.response.PostSummaryResponse;
import org.nova.backend.board.common.application.port.in.BasePostUseCase;
import org.nova.backend.board.common.domain.model.valueobject.PostType;
import org.nova.backend.member.adapter.repository.MemberRepository;
import org.nova.backend.member.domain.exception.MemberDomainException;
import org.nova.backend.member.domain.model.entity.Member;
import org.nova.backend.shared.model.ApiResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "Integrated Post API", description = "통합 게시판 공통 API (QnA, 자유게시판, 자기소개, 공지사항)")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/boards/{boardId}/posts")
public class IntegratedBoardController {
    private final BasePostUseCase basePostUseCase;
    private final MemberRepository memberRepository;


    @PreAuthorize("isAuthenticated()")
    @PostMapping(consumes = {"multipart/form-data"})
    @IntegratedBoardApiDocument.CreatePost
    public ApiResponse<PostDetailResponse> createPost(
            @PathVariable UUID boardId,
            @RequestPart("request") BasePostRequest request,
            @RequestPart(value = "files", required = false) List<MultipartFile> files
    ) {
        UUID memberId = getCurrentMemberId();
        var savedPost = basePostUseCase.createPost(boardId, request, memberId, files);
        return ApiResponse.created(savedPost);
    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping(value = "/{postId}", consumes = {"multipart/form-data"})
    @IntegratedBoardApiDocument.UpdatePost
    public ApiResponse<PostDetailResponse> updatePost(
            @PathVariable UUID boardId,
            @PathVariable UUID postId,
            @RequestPart("request") UpdatePostRequest request,
            @RequestPart(value = "files", required = false) List<MultipartFile> files
    ) {
        UUID memberId = getCurrentMemberId();
        basePostUseCase.updatePost(boardId, postId, request, memberId, files);
        return ApiResponse.noContent();
    }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/{postId}")
    @IntegratedBoardApiDocument.DeletePost
    public ApiResponse<Void> deletePost(
            @PathVariable UUID boardId,
            @PathVariable UUID postId
    ) {
        UUID memberId = getCurrentMemberId();
        basePostUseCase.deletePost(boardId, postId, memberId);
        return ApiResponse.noContent();
    }


    @GetMapping
    @IntegratedBoardApiDocument.GetPostsByCategory
    public ApiResponse<Page<PostSummaryResponse>> getPostsByCategory(
            @PathVariable UUID boardId,
            @RequestParam PostType postType,
            Pageable pageable
    ) {
        var posts = basePostUseCase.getPostsByCategory(boardId, postType, pageable);
        return ApiResponse.success(posts);
    }

    @GetMapping("/{postId}")
    @IntegratedBoardApiDocument.GetPostById
    public ApiResponse<PostDetailResponse> getPostById(
            @PathVariable UUID boardId,
            @PathVariable UUID postId
    ) {
        var post = basePostUseCase.getPostById(boardId, postId);
        return ApiResponse.success(post);
    }

    @GetMapping("/latest")
    @IntegratedBoardApiDocument.GetLatestPostByType
    public ApiResponse<Map<PostType, List<PostSummaryResponse>>> getLatestPostsByType(
            @PathVariable UUID boardId
    ) {
        var latestPosts = basePostUseCase.getLatestPostsByType(boardId);
        return ApiResponse.success(latestPosts);
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