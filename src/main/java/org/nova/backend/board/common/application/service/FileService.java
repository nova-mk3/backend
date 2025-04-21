package org.nova.backend.board.common.application.service;

import jakarta.servlet.http.HttpServletResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Stream;
import org.nova.backend.board.util.ImageOptimizerUtil;
import org.nova.backend.member.domain.exception.MemberDomainException;
import org.nova.backend.shared.constants.FilePathConstants;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.nova.backend.board.util.FileStorageUtil;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.nova.backend.board.common.application.dto.response.FileResponse;
import org.nova.backend.board.common.application.port.in.FileUseCase;
import org.nova.backend.board.common.application.port.out.BasePostPersistencePort;
import org.nova.backend.board.common.application.port.out.FilePersistencePort;
import org.nova.backend.board.common.domain.exception.FileDomainException;
import org.nova.backend.board.common.domain.model.entity.File;
import org.nova.backend.board.common.domain.model.valueobject.PostType;
import org.nova.backend.board.util.FileUtil;
import org.nova.backend.member.adapter.repository.MemberRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
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
    private final ExecutorService executor =
            Executors.newFixedThreadPool(Math.min(6, Runtime.getRuntime().availableProcessors() + 2));
    private final StringRedisTemplate redisTemplate;

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

            String extension = FileUtil.getFileExtension(file.getOriginalFilename());
            String fileName = file.getId() + "." + extension;
            Path publicFilePath = Paths.get(baseFileStoragePath, FilePathConstants.PUBLIC_FOLDER, fileName);
            FileStorageUtil.deleteFile(publicFilePath.toString());
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
            logger.warn("파일이 존재하지 않습니다. ID 목록: {}", fileIds);
        } else {
            logger.info("파일 조회 완료: {}", files);
        }

        return files;
    }

    /**
     * 특정 파일 조회
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<File> findFileById(UUID fileId) {
        return Optional.ofNullable(filePersistencePort.findFileById(fileId)
                .orElseThrow(() -> new FileDomainException("파일을 찾을 수 없습니다. ID: " + fileId)));
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
        memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberDomainException("사용자를 찾을 수 없습니다.", HttpStatus.NOT_FOUND));

        FileUtil.validateFileList(files);

        for (MultipartFile file : files) {
            if (postType == PostType.PICTURES) {
                FileUtil.validateImageFile(file);
            } else {
                FileUtil.validateFileExtension(file);
            }
            FileUtil.validateFileSize(file);
        }

        String storagePath = getStoragePath();

        MultipartFile firstFile = files.getFirst();
        FileResponse firstFileResponse = processFileUpload(firstFile, storagePath, postType, true);

        List<CompletableFuture<FileResponse>> futures = files.stream()
                .skip(1)
                .map(file -> CompletableFuture.supplyAsync(() ->
                        processFileUpload(file, storagePath, postType, false), executor))
                .toList();

        List<FileResponse> result = futures.stream()
                .map(CompletableFuture::join)
                .toList();

        return Stream.concat(
                Stream.of(firstFileResponse),
                result.stream()
        ).toList();
    }

    /**
     * 파일 업로드 처리
     */
    private FileResponse processFileUpload(
            MultipartFile file,
            String storagePath,
            PostType postType,
            boolean isSynchronous
    ) {
        String originalFileName = file.getOriginalFilename();
        String extension = FileUtil.getFileExtension(originalFileName);

        File tempFile = new File(null, originalFileName, null, null, 0);
        File savedFile = filePersistencePort.save(tempFile); // UUID 생성됨
        UUID fileId = savedFile.getId();

        String savedFilePath = FileStorageUtil.saveFileToLocal(file, storagePath, FilePathConstants.PROTECTED_FOLDER, fileId, extension);
        savedFile.setFilePath(savedFilePath);
        savedFile = filePersistencePort.save(savedFile);

        if (postType == PostType.PICTURES) {
            redisTemplate.opsForValue().set("upload:" + fileId, "uploading");

            if (isSynchronous) {
                compressImageSync(fileId, savedFilePath, storagePath, extension);
            } else {
                CompletableFuture.runAsync(() ->
                        compressImageAsync(fileId, savedFilePath, storagePath, extension), executor);
            }
        }

        return new FileResponse(
                savedFile.getId(),
                savedFile.getOriginalFilename(),
                "/api/v1/files/" + savedFile.getId() + "/download"
        );
    }

    private void compressImageAsync(
            UUID fileId,
            String originalFilePath,
            String storagePath,
            String extension
    ) {
        String threadName = Thread.currentThread().getName();
        logger.info("[압축 시작] fileId={} thread={}", fileId, threadName);

        try {
            redisTemplate.opsForValue().set("upload:" + fileId, "compressing");

            Path protectedPath = Paths.get(originalFilePath);
            Path publicDir = Paths.get(storagePath, FilePathConstants.PUBLIC_FOLDER);

            ImageOptimizerUtil.compressToOriginalExtension(protectedPath, publicDir, fileId, extension);

            redisTemplate.opsForValue().set("upload:" + fileId, "done");
            logger.info("[압축 완료] fileId={}", fileId);
        } catch (Exception e) {
            logger.error("이미지 압축 중 오류", e);
            redisTemplate.opsForValue().set("upload:" + fileId, "error");
        }
    }

    private void compressImageSync(
            UUID fileId,
            String originalFilePath,
            String storagePath,
            String extension
    ) {
        try {
            redisTemplate.opsForValue().set("upload:" + fileId, "compressing");

            Path protectedPath = Paths.get(originalFilePath);
            Path publicDir = Paths.get(storagePath, FilePathConstants.PUBLIC_FOLDER);

            ImageOptimizerUtil.compressToOriginalExtension(protectedPath, publicDir, fileId, extension);

            redisTemplate.opsForValue().set("upload:" + fileId, "done");
            logger.info("[동기 압축 완료] 썸네일 fileId={}", fileId);
        } catch (Exception e) {
            logger.error("동기 이미지 압축 중 오류", e);
            redisTemplate.opsForValue().set("upload:" + fileId, "error");
        }
    }

    /**
     * 파일 저장 경로 결정
     */
    private String getStoragePath() {
        return baseFileStoragePath;
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

        Path originalPath = Paths.get(file.getFilePath());
        FileStorageUtil.deleteFile(originalPath.toString());

        String extension = FileUtil.getFileExtension(file.getOriginalFilename());
        Path publicPath = Paths.get(baseFileStoragePath, FilePathConstants.PUBLIC_FOLDER, file.getId() + "." + extension);
        FileStorageUtil.deleteFile(publicPath.toString());

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
                .orElseThrow(() -> new MemberDomainException("사용자를 찾을 수 없습니다.", HttpStatus.NOT_FOUND));

        File file = filePersistencePort.findFileById(fileId)
                .orElseThrow(() -> new FileDomainException("파일을 찾을 수 없습니다."));

        processFileDownload(file, response);
        updateDownloadCount(file);
    }

    /**
     * 파일 다운로드 처리
     */
    private void processFileDownload(File file, HttpServletResponse response) {
        Path filePath = Paths.get(file.getFilePath());
        if (!Files.exists(filePath)) {
            throw new FileDomainException("파일이 존재하지 않습니다.");
        }

        String encodedFileName = FileUtil.encodeFileName(file.getOriginalFilename());

        try {
            long fileSize = Files.size(filePath);
            response.setContentLengthLong(fileSize);
        } catch (IOException e) {
            throw new FileDomainException("파일 크기 조회 중 오류 발생", e);
        }

        response.setContentType(FileUtil.getDefaultContentType());
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, FileUtil.getContentDispositionHeader(encodedFileName));

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