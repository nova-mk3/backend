package org.nova.backend.board.adapter.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.UUID;
import org.nova.backend.board.application.dto.request.BasePostRequest;
import org.nova.backend.board.application.dto.request.UpdatePostRequest;
import org.nova.backend.board.application.dto.response.PostDetailResponse;
import org.nova.backend.board.application.dto.response.PostResponse;
import org.nova.backend.board.application.dto.response.PostSummaryResponse;
import org.nova.backend.board.application.port.in.PostUseCase;
import org.nova.backend.board.domain.model.valueobject.PostType;
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
@RequestMapping("/api/v1/boards/{boardId}/posts")
public class IntegratedBoardController {
    private final PostUseCase postUseCase;
    private final MemberRepository memberRepository;

    public IntegratedBoardController(
            PostUseCase postUseCase,
            MemberRepository memberRepository
    ) {
        this.postUseCase = postUseCase;
        this.memberRepository = memberRepository;
    }

    @Operation(summary = "게시글 생성", description = "새로운 게시글을 생성합니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "게시글 생성 성공", content = @Content(mediaType = "application/json")),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청 데이터", content = @Content(mediaType = "application/json")),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "서버 에러", content = @Content(mediaType = "application/json"))
    })
    @PreAuthorize("isAuthenticated()")
    @PostMapping(consumes = {"multipart/form-data"})
    public ApiResponse<PostResponse> createPost(
            @PathVariable UUID boardId,
            @RequestPart("request") BasePostRequest request,
            @RequestPart(value = "files", required = false) List<MultipartFile> files
    ) {
        UUID memberId = getCurrentMemberId();
        var savedPost = postUseCase.createPost(boardId, request, memberId, files);
        return ApiResponse.created(savedPost);
    }

    @Operation(summary = "게시글 수정", description = "기존 게시글을 수정합니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "204", description = "게시글 수정 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청 데이터", content = @Content(mediaType = "application/json")),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "수정 권한 없음", content = @Content(mediaType = "application/json")),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "게시글을 찾을 수 없음", content = @Content(mediaType = "application/json")),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "서버 에러", content = @Content(mediaType = "application/json"))
    })
    @PreAuthorize("isAuthenticated()")
    @PutMapping(value = "/{postId}", consumes = {"multipart/form-data"})
    public ApiResponse<PostResponse> updatePost(
            @PathVariable UUID boardId,
            @PathVariable UUID postId,
            @RequestPart("request") UpdatePostRequest request,
            @RequestPart(value = "files", required = false) List<MultipartFile> files
    ) {
        UUID memberId = getCurrentMemberId();
        postUseCase.updatePost(boardId, postId, request, memberId, files);
        return ApiResponse.noContent();
    }

    @Operation(summary = "게시글 삭제", description = "게시글을 삭제합니다. (작성자 또는 관리자만 가능)")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "204", description = "게시글 삭제 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "삭제 권한 없음", content = @Content(mediaType = "application/json")),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "게시글을 찾을 수 없음", content = @Content(mediaType = "application/json")),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "서버 에러", content = @Content(mediaType = "application/json"))
    })
    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/{postId}")
    public ApiResponse<Void> deletePost(
            @PathVariable UUID boardId,
            @PathVariable UUID postId
    ) {
        UUID memberId = getCurrentMemberId();
        postUseCase.deletePost(boardId, postId, memberId);
        return ApiResponse.noContent();
    }


    @Operation(summary = "카테고리별 게시글 조회", description = "특정 게시판 카테고리에 속한 게시글 목록을 가져옵니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "게시글 목록 조회 성공", content = @Content(mediaType = "application/json")),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청 데이터", content = @Content(mediaType = "application/json"))
    })
    @GetMapping
    public ApiResponse<Page<PostSummaryResponse>> getPostsByCategory(
            @PathVariable UUID boardId,
            @RequestParam PostType postType,
            Pageable pageable
    ) {
        var posts = postUseCase.getPostsByCategory(boardId, postType, pageable);
        return ApiResponse.success(posts);
    }

    @Operation(summary = "게시글 조회", description = "특정 게시글을 ID를 기반으로 조회합니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "게시글 조회 성공", content = @Content(mediaType = "application/json")),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "게시글을 찾을 수 없음", content = @Content(mediaType = "application/json"))
    })
    @GetMapping("/{postId}")
    public ApiResponse<PostDetailResponse> getPostById(
            @PathVariable UUID boardId,
            @PathVariable UUID postId
    ) {
        var post = postUseCase.getPostById(boardId, postId);
        return ApiResponse.success(post);
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