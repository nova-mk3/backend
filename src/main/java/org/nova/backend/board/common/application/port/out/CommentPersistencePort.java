package org.nova.backend.board.common.application.port.out;

import org.nova.backend.board.common.domain.model.entity.Comment;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CommentPersistencePort {
    Comment save(Comment comment);

    List<Comment> findAllByPostId(UUID postId);

    Optional<Comment> findById(UUID commentId);

    List<Comment> findAllByParentId(UUID parentId);

    long countByParentId(UUID parentId);

    void deleteAllByParentId(UUID parentId);

    void deleteComment(UUID commentId);

    void deleteAllByPostId(UUID postId);
}
