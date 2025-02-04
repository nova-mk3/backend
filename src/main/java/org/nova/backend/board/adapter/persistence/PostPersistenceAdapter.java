package org.nova.backend.board.adapter.persistence;

import jakarta.transaction.Transactional;
import java.util.Optional;
import java.util.UUID;
import org.nova.backend.board.adapter.persistence.repository.PostRepository;
import org.nova.backend.board.application.port.out.PostPersistencePort;
import org.nova.backend.board.domain.model.entity.Post;
import org.nova.backend.board.domain.model.valueobject.PostType;
import org.nova.backend.member.domain.model.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
public class PostPersistenceAdapter implements PostPersistencePort {
    private final PostRepository postRepository;

    public PostPersistenceAdapter(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    @Override
    public Page<Post> findAllByBoardAndCategory(UUID boardId, PostType postType, Pageable pageable) {
        return postRepository.findAllByBoardIdAndPostType(boardId, postType, pageable);
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
    public int likePost(UUID postId, Member member) {
        postRepository.increaseLikeCount(postId);
        return postRepository.getLikeCount(postId);
    }

    @Override
    @Transactional
    public int unlikePost(UUID postId, Member member) {
        postRepository.decreaseLikeCount(postId);
        return postRepository.getLikeCount(postId);
    }

    @Override
    public void deleteById(UUID postId) {
        postRepository.deleteById(postId);
    }
}
