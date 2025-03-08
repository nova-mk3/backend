package org.nova.backend.member.application.mapper;

import lombok.RequiredArgsConstructor;
import org.nova.backend.member.adapter.repository.ProfilePhotoFileRepository;
import org.nova.backend.member.application.dto.response.ProfilePhotoResponse;
import org.nova.backend.member.domain.exception.ProfilePhotoFileDomainException;
import org.nova.backend.member.domain.model.entity.ProfilePhoto;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MemberProfilePhotoMapper {

    private final ProfilePhotoFileRepository profilePhotoFileRepository;

    public ProfilePhotoResponse toResponse(ProfilePhoto profilePhoto) {

        if (profilePhoto == null) {
            profilePhoto = profilePhotoFileRepository.findProfilePhotoByOriginalFilename("base_profile_image.png")
                    .orElseThrow(() -> new ProfilePhotoFileDomainException("기본이미지를 찾을 수 없습니다.", HttpStatus.NOT_FOUND));
        }

        return new ProfilePhotoResponse(
                profilePhoto.getId(),
                profilePhoto.getOriginalFilename(),
                "/api/v1/files/" + profilePhoto.getId() + "/download"
        );
    }
}
