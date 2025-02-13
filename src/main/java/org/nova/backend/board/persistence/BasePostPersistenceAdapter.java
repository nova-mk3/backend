package org.nova.backend.board.persistence;

import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.nova.backend.board.persistence.repository.PostRepository;
import org.nova.backend.board.common.application.port.out.BasePostPersistencePort;
import org.nova.backend.board.common.domain.model.entity.Post;
import org.nova.backend.board.common.domain.model.valueobject.PostType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
public class BasePostPersistenceAdapter implements BasePostPersistencePort {
    private final PostRepository postRepository;

    public BasePostPersistenceAdapter(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    @Override
    public Page<Post> findAllByBoardAndCategory(UUID boardId, PostType postType, Pageable pageable) {
        return postRepository.findAllByBoardIdAndPostType(boardId, postType, pageable);
    }

    @Override
    public Page<Post> findAllByBoard(UUID boardId, Pageable pageable) {
        return postRepository.findAllByBoardId(boardId, pageable);
    }

    @Override
    public Post save(Post post) {
        return postRepository.save(post);
    }

    @Override
    public Optional<Post> findById(UUID postId) {
        return postRepository.findById(postId);
    }

    @Override
    public Optional<Post> findByBoardIdAndPostId(UUID boardId, UUID postId) {
        return postRepository.findByBoardIdAndPostId(boardId, postId);
    }

    @Override
    @Transactional
    public void increaseViewCount(UUID postId) {
        postRepository.increaseViewCount(postId);
    }

    @Override
    @Transactional
    public int likePost(UUID postId, UUID memberId) {
        postRepository.increaseLikeCount(postId);
        return postRepository.getLikeCount(postId);
    }

    @Override
    @Transactional
    public int unlikePost(UUID postId, UUID memberId) {
        postRepository.decreaseLikeCount(postId);
        return postRepository.getLikeCount(postId);
    }

    @Override
    public void deleteById(UUID postId) {
        postRepository.deleteById(postId);
    }

    @Override
    public List<Post> findLatestPostsByType(UUID boardId, PostType postType, int limit) {
        return postRepository.findTop6ByBoardIdAndPostTypeOrderByCreatedTimeDesc(boardId, postType);
    }
}
