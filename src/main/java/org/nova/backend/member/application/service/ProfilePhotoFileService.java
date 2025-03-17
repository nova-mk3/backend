package org.nova.backend.member.application.service;

import jakarta.servlet.http.HttpServletResponse;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.nova.backend.board.common.domain.exception.FileDomainException;
import org.nova.backend.board.util.FileStorageUtil;
import org.nova.backend.board.util.FileUtil;
import org.nova.backend.member.adapter.repository.ProfilePhotoFileRepository;
import org.nova.backend.member.application.dto.response.ProfilePhotoResponse;
import org.nova.backend.member.application.mapper.MemberProfilePhotoMapper;
import org.nova.backend.member.domain.exception.ProfilePhotoFileDomainException;
import org.nova.backend.member.domain.model.entity.ProfilePhoto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StreamUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ProfilePhotoFileService {
    private static final Logger logger = LoggerFactory.getLogger(ProfilePhotoFileService.class);

    private final ProfilePhotoFileRepository profilePhotoFileRepository;
    private final MemberProfilePhotoMapper memberProfilePhotoMapper;

    @Value("${file.storage.path}")
    private String baseFileStoragePath;

    @Value("${base.profile.image.name}")
    private String baseProfilePhotoName;

    /**
     * 프로필 사진 업로드
     */
    @Transactional
    public ProfilePhotoResponse uploadProfilePhoto(
            MultipartFile profilePhoto
    ) {
        if (profilePhoto == null || profilePhoto.isEmpty()) {
            throw new FileDomainException("업로드할 파일이 없습니다.");
        }

        FileUtil.validateImageFile(profilePhoto);
        FileUtil.validateFileSize(profilePhoto);

        String storagePath = Paths.get(baseFileStoragePath, "profile").toString();

        return processProfilePhotoUpload(profilePhoto, storagePath);
    }

    /**
     * 로컬에서 파일 삭제 + DB에서도 삭제
     */
    @Transactional
    public void deleteProfilePhotoById(UUID profilePhotoId) {
        ProfilePhoto profilePhotoToDelete = findProfilePhotoById(profilePhotoId);

        Path filePath = Paths.get(profilePhotoToDelete.getFilePath());
        try {
            Files.deleteIfExists(filePath);
            logger.info("파일 삭제 성공: {}", profilePhotoToDelete.getFilePath());
        } catch (IOException e) {
            logger.error("파일 삭제 실패: {}", profilePhotoToDelete.getFilePath(), e);
        }

        profilePhotoFileRepository.deleteProfilePhotoById(profilePhotoId);
    }

    /**
     * 특정 파일 조회
     */
    public ProfilePhoto findProfilePhotoById(UUID profilePhotoId) {
        return profilePhotoFileRepository.findProfilePhotoById(profilePhotoId)
                .orElseThrow(() -> new ProfilePhotoFileDomainException("프로필 사진을 찾을 수 없습니다.", HttpStatus.NOT_FOUND));
    }


    /**
     * 파일 업로드 처리
     */
    private ProfilePhotoResponse processProfilePhotoUpload(
            MultipartFile file,
            String storagePath
    ) {
        String savedFilePath = FileStorageUtil.saveFileToLocal(file, storagePath);
        ProfilePhoto savedProfilePhoto = new ProfilePhoto(null, file.getOriginalFilename(), savedFilePath);
        savedProfilePhoto = profilePhotoFileRepository.save(savedProfilePhoto);

        return memberProfilePhotoMapper.toResponse(savedProfilePhoto);
    }

    /**
     * 프로필 사진 조회 : profilePhoto를 지정하지 않은 사용자에 대해 기본 이미지를 보여준다.
     *
     * @return profilePhoto
     */

    public ProfilePhoto getProfilePhoto(ProfilePhoto profilePhoto) {
        return profilePhoto == null ? findBaseProfilePhoto() : findProfilePhotoById(profilePhoto.getId());
    }

    /**
     * 기본 프로필 사진 조회
     *
     * @return 기본 프로필 사진
     */
    public ProfilePhoto findBaseProfilePhoto() {
        return profilePhotoFileRepository.findProfilePhotoByOriginalFilename(baseProfilePhotoName)
                .orElseThrow(() -> new ProfilePhotoFileDomainException("기본 이미지를 찾을 수 없습니다.", HttpStatus.NOT_FOUND));
    }

    /**
     * 프로필 사진 다운로드 처리
     */
    private void processProfileDownload(ProfilePhoto profilePhoto, HttpServletResponse response) {

        Path filePath = Paths.get(profilePhoto.getFilePath());
        if (!Files.exists(filePath)) {
            throw new ProfilePhotoFileDomainException("프로필 사진이 존재하지 않습니다.", HttpStatus.NOT_FOUND);
        }

        String encodedFileName = URLEncoder.encode(profilePhoto.getOriginalFilename(), StandardCharsets.UTF_8)
                .replaceAll("\\+", "%20"); // 공백 문제 해결

        response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename*=UTF-8''" + encodedFileName);

        try (InputStream inputStream = new FileInputStream(filePath.toFile())) {
            StreamUtils.copy(inputStream, response.getOutputStream());
            response.flushBuffer();
        } catch (IOException e) {
            throw new FileDomainException("파일 다운로드 중 오류 발생", e);
        }
    }

    /**
     * 프로필 사진 다운로드 (로그인한 사람만 가능)
     */
    public void downloadProfilePhoto(
            UUID profilePhotoId,
            HttpServletResponse response
    ) {
        ProfilePhoto profilePhoto = findProfilePhotoById(profilePhotoId);
        processProfileDownload(profilePhoto, response);
    }
}
