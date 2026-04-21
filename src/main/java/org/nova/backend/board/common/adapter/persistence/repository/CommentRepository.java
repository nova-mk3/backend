package org.nova.backend.board.common.adapter.persistence.repository;

import org.nova.backend.board.common.domain.model.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CommentRepository extends JpaRepository<Comment, UUID> {

    List<Comment> findAllByParentCommentId(UUID parentCommentId);

    List<Comment> findAllByPostIdOrderByCreatedTimeAsc(UUID postId);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("delete from Comment c where c.parentComment.id = :parentCommentId")
    void deleteAllByParentCommentId(@Param("parentCommentId") UUID parentCommentId);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("delete from Comment c where c.id = :commentId")
    void deleteByCommentId(@Param("commentId") UUID commentId);

    long countByParentCommentId(UUID parentCommentId);
}
