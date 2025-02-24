package org.nova.backend.board.common.application.port.out;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.nova.backend.board.common.domain.model.entity.Post;
import org.nova.backend.board.common.domain.model.valueobject.PostType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.repository.query.Param;

public interface BasePostPersistencePort {
    Page<Post> findAllByBoardAndCategory(UUID boardId, PostType postType, Pageable pageable);
    Page<Post> findAllByBoard(UUID boardId, Pageable pageable);
    Post save(Post post);
    List<Post> findLatestPostsByType(UUID boardId, PostType postType, int limit);

    @EntityGraph(attributePaths = {"files"})
    Optional<Post> findById(UUID postId);
    Optional<Post> findByBoardIdAndPostId(UUID boardId, UUID postId);

    void deleteById(UUID postId);
    void increaseViewCount(@Param("postId") UUID postId);
    void increaseLikeCount(@Param("postId") UUID postId);
    void decreaseLikeCount(@Param("postId") UUID postId);
    int getLikeCount(@Param("postId") UUID postId);

    Page<Post> searchAllByBoardId(UUID boardId, String keyword, String searchType, Pageable pageable);
    Page<Post> searchByTitle(UUID boardId, PostType postType, String keyword, Pageable pageable);
    Page<Post> searchByContent(UUID boardId, PostType postType, String keyword, Pageable pageable);
    Page<Post> searchByTitleOrContent(UUID boardId, PostType postType, String keyword, Pageable pageable);
}
