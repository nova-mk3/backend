package org.nova.backend.board.common.adapter.web;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.nova.backend.board.common.adapter.doc.FileApiDocument;
import org.nova.backend.board.common.application.dto.response.FileResponse;
import org.nova.backend.board.common.application.port.in.FileUseCase;
import org.nova.backend.board.common.domain.model.valueobject.PostType;
import org.nova.backend.board.util.SecurityUtil;
import org.nova.backend.shared.model.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "File API", description = "파일 업로드 및 다운로드 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/files")
public class FileController {
    private final FileUseCase fileUseCase;
    private final SecurityUtil securityUtil;

    @PreAuthorize("isAuthenticated()")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @FileApiDocument.UploadFiles
    public ResponseEntity<ApiResponse<List<FileResponse>>> uploadFiles(
            @RequestParam("files") List<MultipartFile> files,
            @RequestParam("postType") PostType postType
    ) {
        UUID memberId = securityUtil.getCurrentMemberId();
        List<FileResponse> fileResponses = fileUseCase.uploadFiles(files, memberId, postType);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created(fileResponses));
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{fileId}/download")
    @FileApiDocument.DownloadFile
    public void downloadFile(
            @PathVariable UUID fileId,
            HttpServletResponse response
    ) {
        UUID memberId = securityUtil.getCurrentMemberId();
        fileUseCase.downloadFile(fileId, response, memberId);
    }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/{fileId}")
    @FileApiDocument.DeleteFile
    public ResponseEntity<ApiResponse<Void>> deleteFile(
            @PathVariable UUID fileId
    ) {
        UUID memberId = securityUtil.getCurrentMemberId();
        fileUseCase.deleteFileById(fileId, memberId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(ApiResponse.noContent());
    }
}
