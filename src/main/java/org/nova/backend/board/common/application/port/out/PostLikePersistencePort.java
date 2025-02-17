package org.nova.backend.board.common.application.port.out;

import java.util.Optional;
import java.util.UUID;
import org.nova.backend.board.common.domain.model.entity.PostLike;

public interface PostLikePersistencePort {
    Optional<PostLike> findByPostIdAndMemberId(UUID postId, UUID memberId);
    void save(PostLike postLike);
    void deleteByPostIdAndMemberId(UUID postId, UUID memberId);
}