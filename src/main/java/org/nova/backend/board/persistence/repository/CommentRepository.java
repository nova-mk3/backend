package org.nova.backend.board.persistence.repository;

import java.util.List;
import java.util.UUID;
import org.nova.backend.board.common.domain.model.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<Comment, UUID> {
    List<Comment> findAllByParentCommentId(UUID parentCommentId);
    List<Comment> findAllByPostIdOrderByCreatedTimeAsc(UUID postId);
}
