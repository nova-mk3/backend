package org.nova.backend.board.suggestion.application.service;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.nova.backend.board.common.domain.exception.BoardDomainException;
import org.nova.backend.board.common.domain.exception.FileDomainException;
import org.nova.backend.board.suggestion.application.port.in.SuggestionFileUseCase;
import org.nova.backend.board.suggestion.application.port.out.SuggestionFilePersistencePort;
import org.nova.backend.board.suggestion.domain.exception.SuggestionFileDomainException;
import org.nova.backend.board.suggestion.domain.model.entity.SuggestionFile;
import org.nova.backend.member.adapter.repository.MemberRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class SuggestionFileService implements SuggestionFileUseCase {
    private static final Logger logger = LoggerFactory.getLogger(SuggestionFileService.class);

    private final SuggestionFilePersistencePort filePersistencePort;
    private final MemberRepository memberRepository;

    @Value("${file.storage.path}")
    private String baseFileStoragePath;

    /**
     * 파일 업로드
     */
    @Transactional
    @Override
    public List<UUID> uploadFiles(
            List<MultipartFile> files,
            UUID postId
    ) {
        if (files == null || files.isEmpty()) {
            throw new SuggestionFileDomainException("업로드할 파일이 없습니다.", HttpStatus.NOT_FOUND);
        }
        if (files.size() > 10) {
            throw new FileDomainException("첨부파일은 최대 10개까지 가능합니다.");
        }
        String storagePath = getStoragePath();

        return files.stream()
                .map(file -> processFileUpload(file, storagePath))
                .collect(Collectors.toList());
    }

    private UUID processFileUpload(
            MultipartFile file,
            String storagePath
    ) {
        String savedFilePath = saveFileToLocal(file, storagePath);
        SuggestionFile savedFile = new SuggestionFile(null, file.getOriginalFilename(), savedFilePath, null);

        return filePersistencePort.save(savedFile).getId();
    }

    private String getStoragePath() {
        return Paths.get(baseFileStoragePath, "post", "SUGGESTION").toString();
    }

    /**
     * 파일 list로 조회
     */
    @Override
    public List<SuggestionFile> findFilesByIds(List<UUID> fileIds) {
        List<SuggestionFile> files = filePersistencePort.findFilesByIds(fileIds);

        if (files.isEmpty()) {
            logger.warn("삭제할 파일이 존재하지 않습니다. ID 목록: {}", fileIds);
        } else {
            logger.info("삭제할 파일 조회 완료: {}", files);
        }

        return files;
    }

    /**
     * 파일 다운로드 (로그인한 사람만 가능)
     */
    @Override
    public void downloadFile(
            UUID fileId,
            HttpServletResponse response,
            UUID memberId
    ) {
        memberRepository.findById(memberId)
                .orElseThrow(() -> new BoardDomainException("사용자를 찾을 수 없습니다."));

        SuggestionFile file = filePersistencePort.findFileById(fileId)
                .orElseThrow(() -> new SuggestionFileDomainException("파일을 찾을 수 없습니다.",HttpStatus.NOT_FOUND));

        processFileDownload(file, response);
    }

    /**
     * 파일 다운로드 처리
     */
    private void processFileDownload(
            SuggestionFile file,
            HttpServletResponse response
    ) {
        Path filePath = Paths.get(file.getFilePath());
        if (!Files.exists(filePath)) {
            throw new SuggestionFileDomainException("파일이 존재하지 않습니다.",HttpStatus.NOT_FOUND);
        }

        response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getOriginalFilename() + "\"");

        try (InputStream inputStream = new FileInputStream(filePath.toFile())) {
            StreamUtils.copy(inputStream, response.getOutputStream());
            response.flushBuffer();
        } catch (IOException e) {
            throw new SuggestionFileDomainException("파일 다운로드 중 오류 발생",HttpStatus.NOT_FOUND);
        }
    }

    /**
     * 로컬에 파일 저장
     */
    private String saveFileToLocal(
            MultipartFile file,
            String storagePath
    ) {
        try {
            Path fileDir = Paths.get(storagePath);
            if (!Files.exists(fileDir)) {
                Files.createDirectories(fileDir);
            }

            String originalFileName = file.getOriginalFilename();
            String safeFileName = UUID.randomUUID() + "_" + originalFileName;
            Path targetPath = fileDir.resolve(safeFileName);

            file.transferTo(targetPath.toFile());
            return targetPath.toString();
        } catch (IOException e) {
            logger.error("파일 저장 중 오류 발생: {}", file.getOriginalFilename(), e);
            throw new SuggestionFileDomainException("파일 저장 중 오류 발생", HttpStatus.NOT_FOUND);
        }
    }
}
