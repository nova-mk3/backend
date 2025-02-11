package org.nova.backend.board.common.application.service;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.nova.backend.board.common.application.port.in.FileUseCase;
import org.nova.backend.board.common.application.port.out.FilePersistencePort;
import org.nova.backend.board.common.domain.exception.BoardDomainException;
import org.nova.backend.board.common.domain.exception.FileDomainException;
import org.nova.backend.board.common.domain.model.entity.File;
import org.nova.backend.board.common.domain.model.entity.Post;
import org.nova.backend.board.common.domain.model.valueobject.BoardCategory;
import org.nova.backend.board.common.domain.model.valueobject.PostType;
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
public class FileService implements FileUseCase {
    private static final Logger logger = LoggerFactory.getLogger(FileService.class);

    private final FilePersistencePort filePersistencePort;
    private final MemberRepository memberRepository;

    @Value("${file.storage.path}")
    private String baseFileStoragePath;

    public FileService(
            FilePersistencePort filePersistencePort,
            MemberRepository memberRepository
    ) {
        this.filePersistencePort = filePersistencePort;
        this.memberRepository = memberRepository;
    }

    /**
     * 파일 저장 (게시판 카테고리 및 포스트 타입별 폴더 자동 생성)
     */
    @Override
    public List<File> saveFiles(
            List<MultipartFile> files,
            Post post
    ) {
        if (files == null || files.isEmpty()) {
            logger.info("첨부파일이 존재하지 않습니다. 저장을 건너뜁니다.");
            return new ArrayList<>();
        }

        if (files.size() > 10) {
            throw new FileDomainException("첨부파일은 최대 10개까지 가능합니다.");
        }

        BoardCategory category = post.getBoard().getCategory();
        PostType postType = post.getPostType();

        List<File> fileEntities = files.stream()
                .map(file -> {
                    FileUtil.validateFileSize(file);
                    FileUtil.validateFileExtension(file);

                    String storagePath = getStoragePath(category, postType);
                    return new File(null, file.getOriginalFilename(), saveFileToLocal(file, storagePath), post, 0);
                })
                .toList();

        fileEntities.forEach(filePersistencePort::save);
        return fileEntities;
    }


    /**
     * 로컬에서 파일 삭제 + DB에서도 삭제
     */
    @Transactional
    @Override
    public void deleteFiles(List<UUID> fileIds) {
        List<File> filesToDelete = filePersistencePort.findFilesByIds(fileIds);

        for (File file : filesToDelete) {
            Path filePath = Paths.get(file.getFilePath());
            try {
                Files.deleteIfExists(filePath);
                logger.info("파일 삭제 성공: {}", file.getFilePath());
            } catch (IOException e) {
                logger.error("파일 삭제 실패: {}", file.getFilePath(), e);
            }
        }
        filePersistencePort.deleteFilesByIds(fileIds);
    }

    /**
     * 삭제할 파일 먼저 조회
     */
    @Override
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

        Path filePath = Paths.get(file.getFilePath());
        if (!Files.exists(filePath)) {
            throw new FileDomainException("파일이 존재하지 않습니다.");
        }

        response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getOriginalFilename() + "\"");

        try (InputStream inputStream = new FileInputStream(filePath.toFile())) {
            StreamUtils.copy(inputStream, response.getOutputStream());
            response.flushBuffer();
            logger.info("파일 다운로드 성공: {}", file.getOriginalFilename());
        } catch (IOException e) {
            logger.error("파일 다운로드 중 오류 발생: {}", file.getOriginalFilename(), e);
            throw new FileDomainException("파일 다운로드 중 오류 발생", e);
        }
    }


    /**
     * 게시판 카테고리 및 포스트 타입별 폴더 경로 반환
     */
    private String getStoragePath(
            BoardCategory category,
            PostType postType
    ) {
        return Paths.get(baseFileStoragePath, "post", category.name(), postType.name()).toString();
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
                logger.info("파일 저장 디렉토리가 생성되었습니다: {}", fileDir.toAbsolutePath());
            }

            String originalFileName = file.getOriginalFilename();
            String safeFileName = UUID.randomUUID() + "_" + originalFileName;
            Path targetPath = fileDir.resolve(safeFileName);

            if (!targetPath.toAbsolutePath().startsWith(fileDir.toAbsolutePath())) {
                throw new FileDomainException("잘못된 파일 경로가 탐지되었습니다.");
            }

            file.transferTo(targetPath.toFile());
            return targetPath.toString();
        } catch (IOException e) {
            logger.error("파일 저장 중 오류 발생: {}", file.getOriginalFilename(), e);
            throw new FileDomainException("파일 저장 중 오류 발생", e);
        }
    }
}
