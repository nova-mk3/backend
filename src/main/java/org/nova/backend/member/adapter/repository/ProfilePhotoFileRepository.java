package org.nova.backend.member.adapter.repository;

import java.util.Optional;
import java.util.UUID;
import org.nova.backend.member.domain.model.entity.ProfilePhoto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ProfilePhotoFileRepository extends JpaRepository<ProfilePhoto, UUID> {
    Optional<ProfilePhoto> findProfilePhotoById(UUID profilePhotoId);

    void deleteProfilePhotoById(UUID profilePhotoId);

    @Query(value = "SELECT p FROM ProfilePhoto p WHERE p.originalFilename = :baseProfilePhotoName ORDER BY p.id ASC LIMIT 1")
    Optional<ProfilePhoto> findProfilePhotoByOriginalFilename(String baseProfilePhotoName);
}
