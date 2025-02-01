package org.nova.backend.board.application.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.nova.backend.board.application.port.in.FileUseCase;
import org.nova.backend.board.application.port.out.FilePersistencePort;
import org.nova.backend.board.domain.exception.FileDomainException;
import org.nova.backend.board.domain.model.entity.File;
import org.nova.backend.board.domain.model.entity.Post;
import org.nova.backend.board.domain.model.valueobject.BoardCategory;
import org.nova.backend.board.domain.model.valueobject.PostType;
import org.nova.backend.board.util.FileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileService implements FileUseCase {
    private static final Logger logger = LoggerFactory.getLogger(FileService.class);

    private final FilePersistencePort filePersistencePort;

    @Value("${file.storage.path}")
    private String baseFileStoragePath;

    public FileService(FilePersistencePort filePersistencePort) {
        this.filePersistencePort = filePersistencePort;
    }

    /**
     * 파일 저장 (게시판 카테고리 및 포스트 타입별 폴더 자동 생성)
     */
    @Override
    public List<File> saveFiles(List<MultipartFile> files, Post post) {
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
     * 게시판 카테고리 및 포스트 타입별 폴더 경로 반환
     */
    private String getStoragePath(BoardCategory category, PostType postType) {
        return Paths.get(baseFileStoragePath, "post", category.name(), postType.name()).toString();
    }

    /**
     * 로컬에 파일 저장
     */
    private String saveFileToLocal(MultipartFile file, String storagePath) {
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
