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

    private final FilePersistencePort filePersistencePort;

    @Value("${file.storage.path:local/storage/path}")
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

        for (MultipartFile multipartFile : files) {
            String filePath = saveFileToLocal(multipartFile);
            File fileEntity = new File(UUID.randomUUID(), filePath, post);
            filePersistencePort.save(fileEntity);
            logger.info("파일 저장 완료: {}", filePath);
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
                logger.info("파일 저장 디렉토리가 생성되었습니다: {}", fileDir);
            }

            String filePath = fileDir.resolve(UUID.randomUUID() + "_" + file.getOriginalFilename()).toString();
            file.transferTo(Paths.get(filePath).toFile());
            return filePath;
        } catch (IOException e) {
            logger.error("파일 저장 중 오류 발생: {}", file.getOriginalFilename(), e);
            throw new FileDomainException("파일 저장 중 오류 발생", e);
        }
    }
}
