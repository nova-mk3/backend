package org.nova.backend.board.adapter.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.nova.backend.board.application.dto.request.BasePostRequest;
import org.nova.backend.board.application.dto.response.PostResponse;
import org.nova.backend.board.application.port.in.PostUseCase;
import org.nova.backend.board.domain.model.valueobject.PostType;
import org.nova.backend.member.adapter.repository.MemberRepository;
import org.nova.backend.member.domain.model.entity.Member;
import org.nova.backend.shared.model.ApiResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "Post API", description = "통합 게시판 공통 API (QnA, 자유게시판, 자기소개, 공지사항)")
@RestController
@RequestMapping("/api/v1/integrated")
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
            @RequestParam("title") String title,
            @RequestParam("content") String content,
            @RequestParam("postType") PostType postType,
            @RequestPart(value = "files", required = false) List<MultipartFile> files
    ) {
        Member member = getCurrentMember();
        var request = new BasePostRequest(title, content, postType);
        var savedPost = postUseCase.createPost(request, member, files);
        return ApiResponse.created(savedPost);
    }

    /**
     * 현재 로그인한 사용자 정보 가져오기
     */
    private Member getCurrentMember() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String studentNumber = authentication.getName();
        return memberRepository.findByStudentNumber(studentNumber)
                .orElseThrow(() -> new IllegalStateException("사용자를 찾을 수 없습니다."));
    }
}