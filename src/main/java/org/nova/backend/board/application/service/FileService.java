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

    //임시경로
    @Value("${file.storage.path:/Users/jiny/Desktop/Project/tmpServer}")
    private String fileStoragePath;

    public FileService(FilePersistencePort filePersistencePort) {
        this.filePersistencePort = filePersistencePort;
    }

    /**
     * 파일 저장
     * @param files 첨부파일 리스트
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

        List<File> fileEntities = files.stream()
                .map(file -> {
                    FileUtil.validateFileSize(file);
                    FileUtil.validateFileExtension(file);

                    return new File(null, file.getOriginalFilename(), saveFileToLocal(file), post, 0);
                })
                .toList();

        fileEntities.forEach(filePersistencePort::save);
        return fileEntities;
    }

    private String saveFileToLocal(MultipartFile file) {
        try {
            Path fileDir = Paths.get(fileStoragePath);
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
