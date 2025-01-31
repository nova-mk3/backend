package org.nova.backend.board.adapter.persistence;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.nova.backend.board.adapter.persistence.repository.CommentRepository;
import org.nova.backend.board.application.port.out.CommentPersistencePort;
import org.nova.backend.board.domain.model.entity.Comment;
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
        return commentRepository.findAllByPostId(postId);
    }

    @Override
    public Optional<Comment> findById(UUID commentId) {
        return commentRepository.findById(commentId);
    }
}
