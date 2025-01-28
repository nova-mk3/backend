package org.nova.backend.board.application.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;
import org.nova.backend.board.application.port.out.FilePersistencePort;
import org.nova.backend.board.domain.exception.FileDomainException;
import org.nova.backend.board.domain.model.entity.File;
import org.nova.backend.board.domain.model.entity.Post;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileService {
    private static final Logger logger = LoggerFactory.getLogger(FileService.class);
    private static final long MAX_FILE_SIZE = 500 * 1024 * 1024;

    private final FilePersistencePort filePersistencePort;

    //임시경로
    @Value("${file.storage.path:/Users/jiny/Desktop/Project/tmpServer}")
    private String fileStoragePath;

    public FileService(FilePersistencePort filePersistencePort) {
        this.filePersistencePort = filePersistencePort;
    }

    /**
     * 파일 저장
     * @param post 게시글 정보
     * @param files 첨부파일 리스트
     */
    public void saveFiles(Post post, List<MultipartFile> files) {
        if (files == null || files.isEmpty()) {
            logger.info("첨부파일이 존재하지 않습니다. 저장을 건너뜁니다.");
            return;
        }

        if (files.size() > 10) {
            throw new FileDomainException("첨부파일은 최대 10개까지 가능합니다.");
        }

        for (MultipartFile file : files) {
            saveFile(post, file);
        }
    }

    /**
     * 동기 파일 저장
     * @param post 게시글 정보
     * @param file MultipartFile
     */
    public void saveFile(Post post, MultipartFile file) {
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new FileDomainException("파일 크기가 너무 큽니다. 파일명: " + file.getOriginalFilename());
        }
        String filePath = saveFileToLocal(file);
        UUID newFileId = UUID.randomUUID();
        File fileEntity = new File(newFileId, filePath, post);

        if (post.getFiles().stream().noneMatch(f -> f.getFilePath().equals(filePath))) {
            post.getFiles().add(fileEntity);
            filePersistencePort.save(fileEntity);
            logger.info("파일 저장 완료: {}", filePath);
        } else {
            logger.warn("중복된 파일이 발견되어 저장을 건너뜁니다: {}", filePath);
        }
    }



    /**
     * 로컬 디스크에 파일 저장
     * @param file MultipartFile
     * @return 저장된 파일 경로
     */
    private String saveFileToLocal(MultipartFile file) {
        try {
            Path fileDir = Paths.get(fileStoragePath);
            if (!Files.exists(fileDir)) {
                Files.createDirectories(fileDir);
                logger.info("파일 저장 디렉토리가 생성되었습니다: {}", fileDir.toAbsolutePath());
            }

            String originalFileName = file.getOriginalFilename();
            String safeFileName = UUID.randomUUID() + "_" + sanitizeFileName(originalFileName);
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

    private String sanitizeFileName(String fileName) {
        return fileName.replaceAll("[^a-zA-Z0-9.\\-]", "_");
    }
}
