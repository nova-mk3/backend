package org.nova.backend.board.util;

import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifIFD0Directory;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.OutputStream;
import java.nio.file.Path;
import java.util.UUID;
import javax.imageio.ImageIO;
import java.nio.file.Files;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.awt.image.AffineTransformOp;
import org.nova.backend.board.common.domain.exception.FileDomainException;

public class ImageOptimizerUtil {
    private static final Logger logger = LoggerFactory.getLogger(ImageOptimizerUtil.class);

    static {
        ImageIO.scanForPlugins();
    }

    /**
     * 이미지 파일을 WebP로 압축하여 저장
     * @param inputPath 원본 이미지 경로
     * @param outputDir 저장 디렉토리 (예: public)
     * @param uuid 고유 파일 이름 생성용 UUID
     * @return 저장된 WebP 파일 경로
     */
    public static Path compressToOriginalExtension(
            Path inputPath,
            Path outputDir,
            UUID uuid,
            String originalExtension
    ) {
        try {
            BufferedImage originalImage = ImageIO.read(inputPath.toFile());
            if (originalImage == null) {
                throw new FileDomainException("이미지 로딩 실패: " + inputPath);
            }

            originalImage = applyExifOrientationFix(inputPath, originalImage);

            int targetWidth = Math.max(1, originalImage.getWidth() / 2);
            int targetHeight = Math.max(1, originalImage.getHeight() / 2);

            Image scaledImage = originalImage.getScaledInstance(targetWidth, targetHeight, Image.SCALE_SMOOTH);
            BufferedImage compressedImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2d = compressedImage.createGraphics();
            g2d.drawImage(scaledImage, 0, 0, null);
            g2d.dispose();

            if (!Files.exists(outputDir)) {
                Files.createDirectories(outputDir);
            }

            Path outputPath = outputDir.resolve(uuid + "." + originalExtension);

            try (OutputStream os = Files.newOutputStream(outputPath)) {
                boolean written = ImageIO.write(compressedImage, originalExtension, os);
                if (!written) {
                    throw new FileDomainException("압축 이미지 저장 실패 (확장자=" + originalExtension + "): " + outputPath);
                }
            }

            logger.info("이미지 압축 저장 완료: {}", outputPath);
            return outputPath;
        } catch (IOException e) {
            logger.error("이미지 압축 중 오류", e);
            throw new FileDomainException("이미지 압축 실패", e);
        }
    }

    public static BufferedImage applyExifOrientationFix(Path inputPath, BufferedImage image) {
        try {
            Metadata metadata = ImageMetadataReader.readMetadata(inputPath.toFile());
            ExifIFD0Directory directory = metadata.getFirstDirectoryOfType(ExifIFD0Directory.class);
            int orientation = 1;
            if (directory != null && directory.containsTag(ExifIFD0Directory.TAG_ORIENTATION)) {
                orientation = directory.getInt(ExifIFD0Directory.TAG_ORIENTATION);
            }

            int w = image.getWidth();
            int h = image.getHeight();

            AffineTransform transform = new AffineTransform();
            switch (orientation) {
                case 6:
                    transform.translate(h, 0);
                    transform.rotate(Math.toRadians(90));
                    break;
                case 3:
                    transform.translate(w, h);
                    transform.rotate(Math.toRadians(180));
                    break;
                case 8:
                    transform.translate(0, w);
                    transform.rotate(Math.toRadians(270));
                    break;
                default:
                    return image;
            }

            AffineTransformOp op = new AffineTransformOp(transform, AffineTransformOp.TYPE_BILINEAR);
            BufferedImage rotatedImage = new BufferedImage(
                    (orientation == 6 || orientation == 8) ? h : w,
                    (orientation == 6 || orientation == 8) ? w : h,
                    image.getType()
            );
            op.filter(image, rotatedImage);
            return rotatedImage;

        } catch (Exception e) {
            logger.warn("EXIF 회전 정보 읽기 실패: 회전 생략", e);
            return image;
        }
    }
}