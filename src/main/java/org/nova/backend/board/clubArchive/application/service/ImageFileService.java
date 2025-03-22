package org.nova.backend.board.clubArchive.application.service;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;
import javax.imageio.ImageIO;
import lombok.RequiredArgsConstructor;
import org.nova.backend.board.clubArchive.application.dto.response.ImageResponse;
import org.nova.backend.board.clubArchive.domain.exception.PictureDomainException;
import org.nova.backend.board.common.application.port.in.FileUseCase;
import org.nova.backend.board.common.domain.model.entity.File;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ImageFileService {
    private final FileUseCase fileUseCase;

    @Value("${file.storage.path}")
    private String baseFileStoragePath;

    @Value("${app.domain}")
    private String appDomain;

    /**
     * 이미지 파일 정보 변환 (width, height 포함)
     */
    public ImageResponse createImageResponse(File file) {
        boolean isImage = isImageFile(file.getOriginalFilename());
        int width = 0, height = 0;

        if (isImage) {
            try {
                BufferedImage image = ImageIO.read(new java.io.File(file.getFilePath()));
                if (image != null) {
                    width = image.getWidth();
                    height = image.getHeight();
                }
            } catch (IOException e) {
                throw new PictureDomainException("이미지 크기 정보를 불러올 수 없습니다.",HttpStatus.BAD_REQUEST);
            }
        }

        String relativeUrl = convertFilePathToUrl(file.getFilePath());
        String imageUrl = appDomain + relativeUrl;

        return new ImageResponse(
                file.getId(),
                file.getOriginalFilename(),
                imageUrl,
                width,
                height
        );
    }

    /**
     * 파일 확장자를 기반으로 이미지 여부 확인
     */
    boolean isImageFile(String filename) {
        String lowerCase = filename.toLowerCase();
        return lowerCase.endsWith(".jpg") || lowerCase.endsWith(".jpeg") || lowerCase.endsWith(".png") || lowerCase.endsWith(".gif") || lowerCase.endsWith(".bmp") || lowerCase.endsWith(".webp");
    }

    /**
     * 특정 게시글의 대표 썸네일 가져오기
     */
    public ImageResponse getThumbnail(List<UUID> fileIds) {
        if (fileIds.isEmpty()) {
            return null;
        }

        File firstImageFile = fileUseCase.findFileById(fileIds.getFirst())
                .orElseThrow(() -> new PictureDomainException("대표 썸네일 이미지를 찾을 수 없습니다.",HttpStatus.NOT_FOUND));

        return createImageResponse(firstImageFile);
    }

    /**
     * 파일 시스템 경로를 웹 URL로 변환
     */
    public String convertFilePathToUrl(String filePath) {
        Path basePath = Paths.get(baseFileStoragePath);
        Path absolutePath = Paths.get(filePath);

        if (absolutePath.startsWith(basePath)) {
            Path relativePath = basePath.relativize(absolutePath);
            return "/files/" + relativePath.toString().replace("\\", "/");
        }
        return "/files/" + absolutePath.getFileName().toString().replace("\\", "/");
    }
}