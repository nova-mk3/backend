package org.nova.backend.board.clubArchive.application.service;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
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
import org.nova.backend.board.util.FileCacheKeyConstants;
import org.nova.backend.board.util.FileUtil;
import org.nova.backend.shared.constants.FilePathConstants;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ImageFileService {
    private final FileUseCase fileUseCase;
    private final StringRedisTemplate redisTemplate;

    @Value("${app.domain}")
    private String appDomain;

    @Value("${file.storage.path}")
    private String fileStoragePath;
    /**
     * 이미지 파일 정보 변환 (width, height 포함)
     */
    public ImageResponse createImageResponse(File file) {
        boolean isImage = isImageFile(file.getOriginalFilename());
        int width = 0;
        int height = 0;

        if (isImage) {
            int[] size = getImageSize(file);
            if (size.length == 2) {
                width = size[0];
                height = size[1];
            }
        }

        String fileName = Paths.get(file.getFilePath()).getFileName().toString();
        String imageUrl = appDomain + FilePathConstants.PUBLIC_FILE_URL_PREFIX + fileName;

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

    private int[] getImageSize(File file) {
        String cacheKey = FileCacheKeyConstants.imageMetaKey(file.getId());
        String cachedSize = redisTemplate.opsForValue().get(cacheKey);
        int[] parsed = parseCachedSize(cachedSize);
        if (parsed.length == 2) {
            return parsed;
        }

        int[] size = readImageSize(file);
        if (size.length == 2 && size[0] > 0 && size[1] > 0) {
            redisTemplate.opsForValue().set(cacheKey, size[0] + "x" + size[1]);
        }
        return size;
    }

    private int[] parseCachedSize(String cachedSize) {
        if (cachedSize == null || cachedSize.isBlank()) {
            return new int[0];
        }
        String[] parts = cachedSize.split("x");
        if (parts.length != 2) {
            return new int[0];
        }
        try {
            return new int[]{Integer.parseInt(parts[0]), Integer.parseInt(parts[1])};
        } catch (NumberFormatException e) {
            return new int[0];
        }
    }

    private int[] readImageSize(File file) {
        String extension = FileUtil.getFileExtension(file.getOriginalFilename());
        Path publicPath = Paths.get(fileStoragePath, FilePathConstants.PUBLIC_FOLDER, file.getId() + "." + extension);
        Path sourcePath = Files.exists(publicPath) ? publicPath : Paths.get(file.getFilePath());

        try {
            BufferedImage image = ImageIO.read(sourcePath.toFile());
            if (image == null) {
                throw new PictureDomainException("이미지 크기 정보를 불러올 수 없습니다.", HttpStatus.BAD_REQUEST);
            }
            return new int[]{image.getWidth(), image.getHeight()};
        } catch (IOException e) {
            throw new PictureDomainException("이미지 크기 정보를 불러올 수 없습니다.", HttpStatus.BAD_REQUEST);
        }
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
}