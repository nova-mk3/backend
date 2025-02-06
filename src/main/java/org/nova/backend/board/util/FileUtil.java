package org.nova.backend.board.util;

import java.util.List;
import org.nova.backend.board.common.domain.exception.FileDomainException;
import org.springframework.web.multipart.MultipartFile;

public class FileUtil {
    private static final long MAX_FILE_SIZE = 50 * 1024 * 1024; // 50MB 제한
    private static final List<String> ALLOWED_EXTENSIONS = List.of(
            "jpg", "jpeg", "png", "gif",  // 이미지 파일
            "pdf", "txt", "docx", "xlsx", "csv", // 문서 파일
            "hwp", "pptx", "ppt", "zip", "tar", "7z", // 추가된 문서 및 압축 파일
            "mp4", "avi", "mov", "wmv", "mkv" // 동영상 파일
    );

    /**
     * 파일 크기 검증
     */
    public static void validateFileSize(MultipartFile file) {
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new FileDomainException("파일 크기가 너무 큽니다. 최대 허용 크기: " + (MAX_FILE_SIZE / (1024 * 1024)) + "MB");
        }
    }

    /**
     * 파일 확장자 검증
     */
    public static void validateFileExtension(MultipartFile file) {
        String fileName = file.getOriginalFilename();
        if (fileName == null || fileName.isBlank()) {
            throw new FileDomainException("파일 이름이 없습니다.");
        }

        String extension = getFileExtension(fileName);
        if (!ALLOWED_EXTENSIONS.contains(extension.toLowerCase())) {
            throw new FileDomainException("허용되지 않은 파일 확장자입니다: " + extension);
        }
    }

    /**
     * 파일 확장자 추출
     */
    private static String getFileExtension(String fileName) {
        int lastIndex = fileName.lastIndexOf('.');
        if (lastIndex == -1) {
            return "";
        }
        return fileName.substring(lastIndex + 1);
    }
}
