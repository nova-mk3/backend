package org.nova.backend.board.persistence;

import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.nova.backend.board.common.application.port.out.PostLikePersistencePort;
import org.nova.backend.board.common.domain.model.entity.PostLike;
import org.nova.backend.board.persistence.repository.PostLikeRepository;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PostLikePersistenceAdapter implements PostLikePersistencePort {
    private final PostLikeRepository postLikeRepository;

    @Override
    public Optional<PostLike> findByPostIdAndMemberId(UUID postId, UUID memberId) {
        return postLikeRepository.findByPostIdAndMemberId(postId, memberId);
    }

    @Override
    public void save(PostLike postLike) {
        postLikeRepository.save(postLike);
    }

    @Override
    public void deleteByPostIdAndMemberId(UUID postId, UUID memberId) {
        postLikeRepository.deleteByPostIdAndMemberId(postId, memberId);
    }
}