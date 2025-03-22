package org.nova.backend.board.util;

import static org.nova.backend.board.util.FileUtil.getFileExtension;

import jakarta.servlet.http.HttpServletResponse;
import org.nova.backend.board.common.domain.exception.FileDomainException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.StreamUtils;
import org.springframework.web.multipart.MultipartFile;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

public class FileStorageUtil {
    private static final Logger logger = LoggerFactory.getLogger(FileStorageUtil.class);

    /**
     * 파일 저장
     */
    public static String saveFileToLocal(
            MultipartFile file,
            String basePath,
            String folderName
    ) {
        try {
            String originalFileName = file.getOriginalFilename();
            if (originalFileName == null) throw new FileDomainException("파일 이름이 없습니다.");

            UUID uuid = UUID.randomUUID();
            String extension = getFileExtension(originalFileName);
            Path fileDir = Paths.get(basePath, folderName);

            if (!Files.exists(fileDir)) {
                Files.createDirectories(fileDir);
                logger.info("파일 저장 디렉토리가 생성되었습니다: {}", fileDir.toAbsolutePath());
            }

            Path targetPath = fileDir.resolve(uuid + "." + extension);
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

    /**
     * 파일 삭제
     */
    public static void deleteFile(String filePath) {
        try {
            Path file = Paths.get(filePath);
            Files.deleteIfExists(file);
            logger.info("파일 삭제 성공: {}", filePath);
        } catch (IOException e) {
            logger.error("파일 삭제 실패: {}", filePath, e);
            throw new FileDomainException("파일 삭제 중 오류 발생", e);
        }
    }

    /**
     * 파일 다운로드 처리
     */
    public static void processFileDownload(
            String filePath,
            String fileName,
            HttpServletResponse response
    ) {
        Path path = Paths.get(filePath);
        if (!Files.exists(path)) {
            throw new FileDomainException("파일이 존재하지 않습니다.");
        }

        response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"");

        try (InputStream inputStream = new FileInputStream(path.toFile())) {
            StreamUtils.copy(inputStream, response.getOutputStream());
            response.flushBuffer();
        } catch (IOException e) {
            throw new FileDomainException("파일 다운로드 중 오류 발생");
        }
    }
}
