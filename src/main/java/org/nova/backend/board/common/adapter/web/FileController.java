package org.nova.backend.board.common.adapter.web;

import jakarta.servlet.http.HttpServletResponse;
import java.util.UUID;
import org.nova.backend.board.common.adapter.doc.FileApiDocument;
import org.nova.backend.board.common.application.port.in.FileUseCase;
import org.nova.backend.member.adapter.repository.MemberRepository;
import org.nova.backend.member.domain.exception.MemberDomainException;
import org.nova.backend.member.domain.model.entity.Member;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/files")
public class FileController {
    private final FileUseCase fileUseCase;
    private final MemberRepository memberRepository;

    public FileController(
            FileUseCase fileUseCase,
            MemberRepository memberRepository
    ){
        this.fileUseCase = fileUseCase;
        this.memberRepository = memberRepository;
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
