package org.nova.backend.board.suggestion.adapter.web;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.nova.backend.board.suggestion.adapter.doc.SuggestionFileApiDocument;
import org.nova.backend.board.suggestion.application.port.in.SuggestionFileUseCase;
import org.nova.backend.member.adapter.repository.MemberRepository;
import org.nova.backend.member.domain.exception.MemberDomainException;
import org.nova.backend.member.domain.model.entity.Member;
import org.nova.backend.shared.model.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "Suggestion Board API", description = "건의게시판 및 파일 업로드, 다운로드 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/suggestion-files")
public class SuggestionFileController {
    private final SuggestionFileUseCase fileUseCase;
    private final MemberRepository memberRepository;

    @PreAuthorize("isAuthenticated()")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @SuggestionFileApiDocument.UploadFiles
    @GetMapping("/upload")
    public ResponseEntity<ApiResponse<List<UUID>>> uploadFiles(
            @RequestParam("files") List<MultipartFile> files
    ) {
        UUID memberId = getCurrentMemberId();
        List<UUID> fileIds = fileUseCase.uploadFiles(files, memberId);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created(fileIds));
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{fileId}/download")
    @SuggestionFileApiDocument.DownloadFile
    public void downloadFile(
            @PathVariable UUID fileId,
            HttpServletResponse response
    ) {
        UUID memberId = getCurrentMemberId();
        fileUseCase.downloadFile(fileId, response, memberId);
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
