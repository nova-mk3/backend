package org.nova.backend.board.application.port.out;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.nova.backend.board.domain.model.entity.Comment;

public interface CommentPersistencePort {
    Comment save(Comment comment);
    List<Comment> findAllByPostId(UUID postId);
    Optional<Comment> findById(UUID commentId);
    void deleteById(UUID commentId);
}
