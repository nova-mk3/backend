package org.nova.backend.board.common.adapter.persistence.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.nova.backend.board.common.domain.model.entity.Post;
import org.nova.backend.board.common.domain.model.valueobject.PostType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends JpaRepository<Post, UUID> {
    Page<Post> findAllByBoardIdAndPostType(UUID boardId, PostType postType, Pageable pageable);

    @Query("SELECT p FROM Post p WHERE p.board.id = :boardId ORDER BY p.createdTime DESC")
    Page<Post> findAllByBoardId(@Param("boardId") UUID boardId, Pageable pageable);

    List<Post> findTop6ByBoardIdAndPostTypeOrderByCreatedTimeDesc(UUID boardId, PostType postType);

    @Query("SELECT p FROM Post p WHERE p.board.id = :boardId AND p.id = :postId")
    Optional<Post> findByBoardIdAndPostId(@Param("boardId") UUID boardId, @Param("postId") UUID postId);

    @Modifying
    @Query("UPDATE Post p SET p.viewCount = p.viewCount + 1 WHERE p.id = :postId")
    void increaseViewCount(@Param("postId") UUID postId);

    @Modifying
    @Query("UPDATE Post p Set p.likeCount = p.likeCount + 1 WHERE p.id = :postId")
    void increaseLikeCount(@Param("postId") UUID postId);

    @Modifying
    @Query("UPDATE Post p SET p.likeCount = p.likeCount - 1 WHERE p.id = :postId AND p.likeCount > 0")
    void decreaseLikeCount(@Param("postId") UUID postId);

    @Query("SELECT p.likeCount FROM Post p WHERE p.id = :postId")
    int getLikeCount(@Param("postId") UUID postId);

    @Query("SELECT p FROM Post p WHERE p.postType <> 'EXAM_ARCHIVE'")
    Page<Post> findAll(Pageable pageable);


    @Query("SELECT p FROM Post p WHERE p.board.id = :boardId AND p.postType = :postType " +
            "AND LOWER(p.title) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Post> searchByTitle(
            @Param("boardId") UUID boardId,
            @Param("postType") PostType postType,
            @Param("keyword") String keyword,
            Pageable pageable
    );

    @Query("SELECT p FROM Post p WHERE p.board.id = :boardId AND p.postType = :postType " +
            "AND LOWER(p.content) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Post> searchByContent(
            @Param("boardId") UUID boardId,
            @Param("postType") PostType postType,
            @Param("keyword") String keyword,
            Pageable pageable
    );

    @Query("SELECT p FROM Post p WHERE p.board.id = :boardId AND p.postType = :postType " +
            "AND (LOWER(p.title) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(p.content) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Post> searchByTitleOrContent(
            @Param("boardId") UUID boardId,
            @Param("postType") PostType postType,
            @Param("keyword") String keyword,
            Pageable pageable
    );

    @Query("SELECT p FROM Post p WHERE p.board.id = :boardId " +
            "AND LOWER(p.title) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Post> searchByTitleInBoard(
            @Param("boardId") UUID boardId,
            @Param("keyword") String keyword,
            Pageable pageable
    );

    @Query("SELECT p FROM Post p WHERE p.board.id = :boardId " +
            "AND LOWER(p.content) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Post> searchByContentInBoard(
            @Param("boardId") UUID boardId,
            @Param("keyword") String keyword,
            Pageable pageable
    );

    @Query("SELECT p FROM Post p WHERE p.board.id = :boardId " +
            "AND (LOWER(p.title) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(p.content) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Post> searchByTitleOrContentInBoard(
            @Param("boardId") UUID boardId,
            @Param("keyword") String keyword,
            Pageable pageable
    );
}
