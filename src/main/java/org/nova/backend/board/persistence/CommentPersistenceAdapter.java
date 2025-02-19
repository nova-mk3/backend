package org.nova.backend.board.persistence;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.nova.backend.board.persistence.repository.CommentRepository;
import org.nova.backend.board.common.application.port.out.CommentPersistencePort;
import org.nova.backend.board.common.domain.model.entity.Comment;
import org.springframework.stereotype.Component;

@Component
public class CommentPersistenceAdapter implements CommentPersistencePort {
    private final CommentRepository commentRepository;

    public CommentPersistenceAdapter(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    @Override
    public Comment save(Comment comment) {
        return commentRepository.save(comment);
    }

    @Override
    public List<Comment> findAllByPostId(UUID postId) {
        return commentRepository.findAllByPostIdOrderByCreatedTimeAsc(postId);
    }

    @Override
    public Optional<Comment> findById(UUID commentId) {
        return commentRepository.findById(commentId);
    }

    @Override
    public void deleteById(UUID commentId) {
        commentRepository.deleteById(commentId);
    }

    @Override
    public List<Comment> findAllByParentId(UUID parentId) {
        return commentRepository.findAllByParentCommentId(parentId);
    }
}
