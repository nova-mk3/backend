package org.nova.backend.board.clubArchive.adapter.web;

import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.nova.backend.board.clubArchive.adapter.doc.JokboBoardApiDocument;
import org.nova.backend.board.clubArchive.application.dto.request.JokboPostRequest;
import org.nova.backend.board.clubArchive.application.dto.request.UpdateJokboPostRequest;
import org.nova.backend.board.clubArchive.application.dto.response.JokboPostDetailResponse;
import org.nova.backend.board.clubArchive.application.dto.response.JokboPostSummaryResponse;
import org.nova.backend.board.clubArchive.application.port.in.JokboPostUseCase;
import org.nova.backend.board.clubArchive.domain.model.valueobject.Semester;
import org.nova.backend.board.util.SecurityUtil;
import org.nova.backend.shared.model.ApiResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "ExamArchive Post API", description = "족보 게시판 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/boards/{boardId}/exam-posts")
public class JokboBoardController {
    private final JokboPostUseCase jokboPostUseCase;
    private final SecurityUtil securityUtil;

    @PreAuthorize("isAuthenticated()")
    @PostMapping
    @JokboBoardApiDocument.CreatePost
    public ResponseEntity<ApiResponse<JokboPostDetailResponse>> createPost(
            @PathVariable UUID boardId,
            @RequestBody JokboPostRequest request
    ) {
        UUID memberId = securityUtil.getCurrentMemberId();
        var savedPost = jokboPostUseCase.createPost(boardId, request, memberId);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created(savedPost));
    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping(value = "/{postId}")
    @JokboBoardApiDocument.UpdatePost
    public ResponseEntity<ApiResponse<JokboPostDetailResponse>> updatePost(
            @PathVariable UUID boardId,
            @PathVariable UUID postId,
            @RequestBody UpdateJokboPostRequest request
    ) {
        UUID memberId = securityUtil.getCurrentMemberId();
        JokboPostDetailResponse updatedPost = jokboPostUseCase.updatePost(boardId, postId, request, memberId);
        return ResponseEntity.ok(ApiResponse.success(updatedPost));
    }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/{postId}")
    @JokboBoardApiDocument.DeletePost
    public ResponseEntity<ApiResponse<Void>> deletePost(
            @PathVariable UUID boardId,
            @PathVariable UUID postId
    ) {
        UUID memberId = securityUtil.getCurrentMemberId();
        jokboPostUseCase.deletePost(boardId, postId, memberId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(ApiResponse.noContent());
    }

    @GetMapping("/{postId}")
    @JokboBoardApiDocument.GetPostById
    public ResponseEntity<ApiResponse<JokboPostDetailResponse>> getPostById(
            @PathVariable UUID boardId,
            @PathVariable UUID postId
    ) {
        var post = jokboPostUseCase.getPostById(boardId, postId);
        return ResponseEntity.ok(ApiResponse.success(post));
    }

    @GetMapping
    @JokboBoardApiDocument.GetPostsByFilter
    public ResponseEntity<ApiResponse<Page<JokboPostSummaryResponse>>> getPostsByFilter(
            @PathVariable UUID boardId,
            @RequestParam(required = false) String professorName,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Semester semester,
            Pageable pageable
    ) {
        var posts = jokboPostUseCase.getPostsByFilter(boardId, professorName, year, semester, pageable);
        return ResponseEntity.ok(ApiResponse.success(posts));
    }
}
