package org.nova.backend.member.application.mapper;

import lombok.RequiredArgsConstructor;
import org.nova.backend.board.clubArchive.application.service.ImageFileService;
import org.nova.backend.member.adapter.repository.ProfilePhotoFileRepository;
import org.nova.backend.member.application.dto.response.ProfilePhotoResponse;
import org.nova.backend.member.domain.exception.ProfilePhotoFileDomainException;
import org.nova.backend.member.domain.model.entity.ProfilePhoto;
import org.nova.backend.shared.constants.FilePathConstants;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.nio.file.Paths;

@Component
@RequiredArgsConstructor
public class MemberProfilePhotoMapper {

    private final ProfilePhotoFileRepository profilePhotoFileRepository;
    private final ImageFileService imageFileService;

    @Value("${app.domain}")
    private String appDomain;

    public ProfilePhotoResponse toResponse(ProfilePhoto profilePhoto) {

        if (profilePhoto == null) {
            profilePhoto = profilePhotoFileRepository.findProfilePhotoByOriginalFilename("base_profile_image.png")
                    .orElseThrow(() -> new ProfilePhotoFileDomainException("기본이미지를 찾을 수 없습니다.", HttpStatus.NOT_FOUND));
        }

        String fileName = Paths.get(profilePhoto.getFilePath()).getFileName().toString();
        String imageUrl = appDomain + FilePathConstants.PUBLIC_FILE_URL_PREFIX + fileName;

        return new ProfilePhotoResponse(
                profilePhoto.getId(),
                profilePhoto.getOriginalFilename(),
                imageUrl
        );
    }
}
