package org.nova.backend.board.common.application.service;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.transaction.annotation.Transactional;
import org.nova.backend.board.util.FileStorageUtil;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.nova.backend.board.common.application.dto.response.FileResponse;
import org.nova.backend.board.common.application.port.in.FileUseCase;
import org.nova.backend.board.common.application.port.out.BasePostPersistencePort;
import org.nova.backend.board.common.application.port.out.FilePersistencePort;
import org.nova.backend.board.common.domain.exception.BoardDomainException;
import org.nova.backend.board.common.domain.exception.FileDomainException;
import org.nova.backend.board.common.domain.model.entity.File;
import org.nova.backend.board.common.domain.model.valueobject.PostType;
import org.nova.backend.board.util.FileStorageUtil;
import org.nova.backend.board.util.FileUtil;
import org.nova.backend.member.adapter.repository.MemberRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class FileService implements FileUseCase {
    private static final Logger logger = LoggerFactory.getLogger(FileService.class);

    private final FilePersistencePort filePersistencePort;
    private final BasePostPersistencePort basePostPersistencePort;
    private final MemberRepository memberRepository;

    @Value("${file.storage.path}")
    private String baseFileStoragePath;

    /**
     * 로컬에서 파일 삭제 + DB에서도 삭제
     */
    @Transactional
    @Override
    public void deleteFiles(List<UUID> fileIds) {
        List<File> filesToDelete = filePersistencePort.findFilesByIds(fileIds);
        for (File file : filesToDelete) {
            FileStorageUtil.deleteFile(file.getFilePath());
        }
        filePersistencePort.deleteFilesByIds(fileIds);
    }

    /**
     * 파일 list로 조회
     */
    @Override
    @Transactional(readOnly = true)
    public List<File> findFilesByIds(List<UUID> fileIds) {
        List<File> files = filePersistencePort.findFilesByIds(fileIds);

        if (files.isEmpty()) {
            logger.warn("삭제할 파일이 존재하지 않습니다. ID 목록: {}", fileIds);
        } else {
            logger.info("삭제할 파일 조회 완료: {}", files);
        }

        return files;
    }

    /**
     * 특정 파일 조회
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<File> findFileById(UUID fileId) {
        return filePersistencePort.findFileById(fileId);
    }

    /**
     * 파일 업로드
     */
    @Transactional
    @Override
    public List<FileResponse> uploadFiles(
            List<MultipartFile> files,
            UUID memberId,
            PostType postType
    ) {
        FileUtil.validateFileList(files);

        for (MultipartFile file : files) {
            if (postType == PostType.PICTURES) {
                FileUtil.validateImageFile(file);
            } else {
                FileUtil.validateFileExtension(file);
            }
            FileUtil.validateFileSize(file);
        }

        String storagePath = getStoragePath(postType);

        return files.stream()
                .map(file -> processFileUpload(file, storagePath))
                .collect(Collectors.toList());
    }

    /**
     * 파일 업로드 처리
     */
    private FileResponse processFileUpload(
            MultipartFile file,
            String storagePath
    ) {
        String savedFilePath = FileStorageUtil.saveFileToLocal(file, storagePath);
        File savedFile = new File(null, file.getOriginalFilename(), savedFilePath, null, 0);
        savedFile = filePersistencePort.save(savedFile);

        return new FileResponse(
                savedFile.getId(),
                savedFile.getOriginalFilename(),
                "/api/v1/files/" + savedFile.getId() + "/download"
        );

    }

    /**
     * 파일 저장 경로 결정 (PostType 기준)
     */
    private String getStoragePath(PostType postType) {
        return Paths.get(baseFileStoragePath, "post", postType.name()).toString();
    }

    /**
     * 단일 파일 삭제
     */
    @Transactional
    @Override
    public void deleteFileById(
            UUID fileId,
            UUID memberId
    ) {
        File file = filePersistencePort.findFileById(fileId)
                .orElseThrow(() -> new FileDomainException("파일을 찾을 수 없습니다."));

        if (file.getPost() != null) {
            UUID postOwnerId = file.getPost().getMember().getId();

            if (!postOwnerId.equals(memberId)) {
                throw new FileDomainException("게시글 작성자만 파일을 삭제할 수 있습니다.");
            }
        }

        Path filePath = Paths.get(file.getFilePath());
        try {
            Files.deleteIfExists(filePath);
            logger.info("파일 삭제 성공: {}", file.getFilePath());
        } catch (IOException e) {
            logger.error("파일 삭제 실패: {}", file.getFilePath(), e);
        }

        filePersistencePort.deleteFileById(fileId);
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

        File file = filePersistencePort.findFileById(fileId)
                .orElseThrow(() -> new FileDomainException("파일을 찾을 수 없습니다."));

        processFileDownload(file, response);
        updateDownloadCount(file);
    }

    /**
     * 파일 다운로드 처리
     */
    private void processFileDownload(
            File file,
            HttpServletResponse response
    ) {
        Path filePath = Paths.get(file.getFilePath());
        if (!Files.exists(filePath)) {
            throw new FileDomainException("파일이 존재하지 않습니다.");
        }

        response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getOriginalFilename() + "\"");

        try (InputStream inputStream = new FileInputStream(filePath.toFile())) {
            StreamUtils.copy(inputStream, response.getOutputStream());
            response.flushBuffer();
        } catch (IOException e) {
            throw new FileDomainException("파일 다운로드 중 오류 발생", e);
        }
    }

    /**
     * 다운로드 횟수 증가
     */
    private void updateDownloadCount(File file) {
        file.incrementDownloadCount();
        if (file.getPost() != null) {
            file.getPost().incrementTotalDownloadCount();
            basePostPersistencePort.save(file.getPost());
        }
        filePersistencePort.save(file);
    }
}
