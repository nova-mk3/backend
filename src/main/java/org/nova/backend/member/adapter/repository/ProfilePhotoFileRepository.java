package org.nova.backend.member.adapter.repository;

import java.util.Optional;
import java.util.UUID;
import org.nova.backend.member.domain.model.entity.ProfilePhoto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProfilePhotoFileRepository extends JpaRepository<ProfilePhoto, UUID> {
    Optional<ProfilePhoto> findProfilePhotoById(UUID profilePhotoId);

    void deleteProfilePhotoById(UUID profilePhotoId);

    Optional<ProfilePhoto> findProfilePhotoByOriginalFilename(String baseProfilePhotoName);
}
