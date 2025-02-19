package org.nova.backend.member.application.mapper;

import org.nova.backend.member.application.dto.response.ProfilePhotoResponse;
import org.nova.backend.member.domain.model.entity.ProfilePhoto;
import org.springframework.stereotype.Component;

@Component
public class MemberProfilePhotoMapper {

    public ProfilePhotoResponse toResponse(ProfilePhoto profilePhoto) {
        return new ProfilePhotoResponse(
                profilePhoto.getId(),
                profilePhoto.getOriginalFilename(),
                "/api/v1/files/" + profilePhoto.getId() + "/download"
        );
    }
}
