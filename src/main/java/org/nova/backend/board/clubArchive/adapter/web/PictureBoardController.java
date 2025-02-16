package org.nova.backend.board.clubArchive.adapter.web;

import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.nova.backend.board.clubArchive.adapter.doc.PictureBoardApiDocument;
import org.nova.backend.board.clubArchive.application.dto.request.PicturePostRequest;
import org.nova.backend.board.clubArchive.application.dto.request.UpdatePicturePostRequest;
import org.nova.backend.board.clubArchive.application.dto.response.PicturePostDetailResponse;
import org.nova.backend.board.clubArchive.application.port.in.PicturePostUseCase;
import org.nova.backend.member.adapter.repository.MemberRepository;
import org.nova.backend.member.domain.exception.MemberDomainException;
import org.nova.backend.member.domain.model.entity.Member;
import org.nova.backend.shared.model.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Picture Board API", description = "사진 게시판 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/boards/{boardId}/picture-posts")
public class PictureBoardController {
    private final PicturePostUseCase picturePostUseCase;
    private final MemberRepository memberRepository;

    /**
     * 사진 게시글 생성
     */
    @PreAuthorize("isAuthenticated()")
    @PostMapping
    @PictureBoardApiDocument.CreatePost
    public ResponseEntity<ApiResponse<PicturePostDetailResponse>> createPost(
            @PathVariable UUID boardId,
            @RequestBody PicturePostRequest request
    ) {
        UUID memberId = getCurrentMemberId();
        var savedPost = picturePostUseCase.createPost(boardId, request, memberId);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created(savedPost));
    }

    /**
     * 사진 게시글 수정
     */
    @PreAuthorize("isAuthenticated()")
    @PutMapping("/{postId}")
    @PictureBoardApiDocument.UpdatePost
    public ResponseEntity<ApiResponse<Void>> updatePost(
            @PathVariable UUID boardId,
            @PathVariable UUID postId,
            @RequestBody UpdatePicturePostRequest request
    ) {
        UUID memberId = getCurrentMemberId();
        picturePostUseCase.updatePost(boardId, postId, request, memberId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(ApiResponse.noContent());
    }

    /**
     * 사진 게시글 삭제
     */
    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/{postId}")
    @PictureBoardApiDocument.DeletePost
    public ResponseEntity<ApiResponse<Void>> deletePost(
            @PathVariable UUID boardId,
            @PathVariable UUID postId
    ) {
        UUID memberId = getCurrentMemberId();
        picturePostUseCase.deletePost(boardId, postId, memberId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(ApiResponse.noContent());
    }

    /**
     * 특정 사진 게시글 조회
     */
    @GetMapping("/{postId}")
    @PictureBoardApiDocument.GetPostById
    public ResponseEntity<ApiResponse<PicturePostDetailResponse>> getPostById(
            @PathVariable UUID boardId,
            @PathVariable UUID postId
    ) {
        var post = picturePostUseCase.getPostById(boardId, postId);
        return ResponseEntity.ok(ApiResponse.success(post));
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