package org.nova.backend.board.examarchive.adapter.web;

import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.nova.backend.board.examarchive.adapter.doc.JokboBoardApiDocument;
import org.nova.backend.board.examarchive.application.dto.request.JokboPostRequest;
import org.nova.backend.board.examarchive.application.dto.request.UpdateJokboPostRequest;
import org.nova.backend.board.examarchive.application.dto.response.JokboPostDetailResponse;
import org.nova.backend.board.examarchive.application.dto.response.JokboPostSummaryResponse;
import org.nova.backend.board.examarchive.application.port.in.JokboPostUseCase;
import org.nova.backend.board.examarchive.domain.model.valueobject.Semester;
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
import org.springframework.web.bind.annotation.*;

@Tag(name = "ExamArchive Post API", description = "족보 게시판 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/boards/{boardId}/exam-posts")
public class JokboBoardController {
    private final JokboPostUseCase jokboPostUseCase;
    private final MemberRepository memberRepository;

    @PreAuthorize("isAuthenticated()")
    @PostMapping
    @JokboBoardApiDocument.CreatePost
    public ApiResponse<JokboPostDetailResponse> createPost(
            @PathVariable UUID boardId,
            @RequestBody JokboPostRequest request
    ) {
        UUID memberId = getCurrentMemberId();
        var savedPost = jokboPostUseCase.createPost(boardId, request, memberId);
        return ApiResponse.created(savedPost);
    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping(value = "/{postId}")
    @JokboBoardApiDocument.UpdatePost
    public ApiResponse<JokboPostDetailResponse> updatePost(
            @PathVariable UUID boardId,
            @PathVariable UUID postId,
            @RequestBody UpdateJokboPostRequest request
    ) {
        UUID memberId = getCurrentMemberId();
        jokboPostUseCase.updatePost(boardId, postId, request, memberId);
        return ApiResponse.noContent();
    }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/{postId}")
    @JokboBoardApiDocument.DeletePost
    public ApiResponse<Void> deletePost(
            @PathVariable UUID boardId,
            @PathVariable UUID postId
    ) {
        UUID memberId = getCurrentMemberId();
        jokboPostUseCase.deletePost(boardId, postId, memberId);
        return ApiResponse.noContent();
    }

    @GetMapping("/{postId}")
    @JokboBoardApiDocument.GetPostById
    public ApiResponse<JokboPostDetailResponse> getPostById(
            @PathVariable UUID boardId,
            @PathVariable UUID postId
    ) {
        var post = jokboPostUseCase.getPostById(boardId, postId);
        return ApiResponse.success(post);
    }

    @GetMapping
    @JokboBoardApiDocument.GetPostsByFilter
    public ApiResponse<Page<JokboPostSummaryResponse>> getPostsByFilter(
            @PathVariable UUID boardId,
            @RequestParam(required = false) String professorName,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Semester semester,
            Pageable pageable
    ) {
        var posts = jokboPostUseCase.getPostsByFilter(boardId, professorName, year, semester, pageable);
        return ApiResponse.success(posts);
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
