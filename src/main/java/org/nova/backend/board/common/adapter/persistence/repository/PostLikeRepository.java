package org.nova.backend.board.common.adapter.persistence.repository;

import org.nova.backend.board.common.domain.model.entity.PostLike;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PostLikeRepository extends JpaRepository<PostLike, UUID> {
    Optional<PostLike> findByPostIdAndMemberId(UUID postId, UUID memberId);
    void deleteByPostIdAndMemberId(UUID postId, UUID memberId);
}