package org.nova.backend.board.common.adapter.web;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.nova.backend.board.common.adapter.doc.FileApiDocument;
import org.nova.backend.board.common.application.port.in.FileUseCase;
import org.nova.backend.member.adapter.repository.MemberRepository;
import org.nova.backend.member.domain.exception.MemberDomainException;
import org.nova.backend.member.domain.model.entity.Member;
import org.nova.backend.shared.model.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "File API", description = "파일 업로드 및 다운로드 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/files")
public class FileController {
    private final FileUseCase fileUseCase;
    private final MemberRepository memberRepository;

    @PreAuthorize("isAuthenticated()")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @FileApiDocument.UploadFiles
    public ApiResponse<List<UUID>> uploadFiles(
            @RequestPart("files") List<MultipartFile> files
    ) {
        UUID memberId = getCurrentMemberId();
        List<UUID> fileIds = fileUseCase.uploadFiles(files, memberId);
        return ApiResponse.success(fileIds);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{fileId}/download")
    @FileApiDocument.DownloadFile
    public void downloadFile(
            @PathVariable UUID fileId,
            HttpServletResponse response
    ) {
        UUID memberId = getCurrentMemberId();
        fileUseCase.downloadFile(fileId, response, memberId);
    }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/{fileId}")
    @FileApiDocument.DeleteFile
    public ApiResponse<Void> deleteFile(
            @PathVariable UUID fileId
    ) {
        UUID memberId = getCurrentMemberId();
        fileUseCase.deleteFileById(fileId, memberId);
        return ApiResponse.noContent();
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
