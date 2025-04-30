package org.nova.backend.board.common.adapter.persistence;

import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.nova.backend.board.common.adapter.persistence.repository.PostRepository;
import org.nova.backend.board.common.application.port.out.BasePostPersistencePort;
import org.nova.backend.board.common.domain.model.entity.Post;
import org.nova.backend.board.common.domain.model.valueobject.PostType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BasePostPersistenceAdapter implements BasePostPersistencePort {
    private final PostRepository postRepository;

    @Override
    public Page<Post> findAllByBoardAndCategory(UUID boardId, PostType postType, Pageable pageable) {
        return postRepository.findAllByBoardIdAndPostType(boardId, postType, pageable);
    }

    @Override
    public Page<Post> findAllByBoard(UUID boardId, Pageable pageable) {
        return postRepository.findAllByBoardId(boardId, pageable);
    }

    @Override
    public Page<Post> findAll(Pageable pageable) {
        return postRepository.findAll(pageable);
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
    public Page<Post> findAllByMemberId(UUID memberId, Pageable pageable) {
        return postRepository.findAllByMemberId(memberId, pageable);
    }

    @Override
    @Transactional
    public void increaseViewCount(UUID postId) {
        postRepository.increaseViewCount(postId);
    }

    @Override
    @Transactional
    public void increaseLikeCount(UUID postId) {
        postRepository.increaseLikeCount(postId);
    }

    @Override
    @Transactional
    public void decreaseLikeCount(UUID postId) {
        postRepository.decreaseLikeCount(postId);
    }

    @Override
    public int getLikeCount(UUID postId) {
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

    @Override
    public Page<Post> searchAllByBoardId(UUID boardId, String keyword, String searchType, Pageable pageable) {
        return switch (searchType.toUpperCase()) {
            case "TITLE" -> postRepository.searchByTitleInBoard(boardId, keyword, pageable);
            case "CONTENT" -> postRepository.searchByContentInBoard(boardId, keyword, pageable);
            default -> postRepository.searchByTitleOrContentInBoard(boardId, keyword, pageable);
        };
    }

    @Override
    public Page<Post> searchByTitle(UUID boardId, PostType postType, String keyword, Pageable pageable) {
        return postRepository.searchByTitle(boardId, postType, keyword, pageable);
    }

    @Override
    public Page<Post> searchByContent(UUID boardId, PostType postType, String keyword, Pageable pageable) {
        return postRepository.searchByContent(boardId, postType, keyword, pageable);
    }

    @Override
    public Page<Post> searchByTitleOrContent(UUID boardId, PostType postType, String keyword, Pageable pageable) {
        return postRepository.searchByTitleOrContent(boardId, postType, keyword, pageable);
    }
}
