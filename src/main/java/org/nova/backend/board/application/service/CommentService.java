package org.nova.backend.board.application.service;

import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.nova.backend.board.application.dto.request.CommentRequest;
import org.nova.backend.board.application.dto.response.CommentResponse;
import org.nova.backend.board.application.mapper.CommentMapper;
import org.nova.backend.board.application.port.in.CommentUseCase;
import org.nova.backend.board.application.port.out.CommentPersistencePort;
import org.nova.backend.board.application.port.out.PostPersistencePort;
import org.nova.backend.board.domain.exception.BoardDomainException;
import org.nova.backend.board.domain.exception.CommentDomainException;
import org.nova.backend.board.domain.model.entity.Comment;
import org.nova.backend.board.domain.model.entity.Post;
import org.nova.backend.member.domain.model.entity.Member;
import org.springframework.stereotype.Service;

@Service
public class CommentService implements CommentUseCase {
    private final CommentPersistencePort commentPersistencePort;
    private final PostPersistencePort postPersistencePort;
    private final CommentMapper commentMapper;

    public CommentService(
            CommentPersistencePort commentPersistencePort,
            PostPersistencePort postPersistencePort,
            CommentMapper commentMapper
    ) {
        this.commentPersistencePort = commentPersistencePort;
        this.postPersistencePort = postPersistencePort;
        this.commentMapper = commentMapper;
    }

    /**
     * 댓글 작성 (대댓글 포함)
     */
    @Override
    @Transactional
    public CommentResponse addComment(CommentRequest request, Member member) {
        Post post = postPersistencePort.findById(request.getPostId())
                .orElseThrow(() -> new BoardDomainException("게시글을 찾을 수 없습니다."));

        Comment parentComment = null;
        if (request.getParentCommentId() != null) {
            parentComment = commentPersistencePort.findById(request.getParentCommentId())
                    .orElseThrow(() -> new CommentDomainException("부모 댓글을 찾을 수 없습니다."));
        }

        Comment comment = new Comment(
                null,
                post,
                member,
                parentComment,
                request.getContent(),
                LocalDateTime.now(),
                null
        );

        comment = commentPersistencePort.save(comment);
        return commentMapper.toResponse(comment);
    }

    /**
     * 특정 게시글의 댓글 조회
     */
    @Override
    public List<CommentResponse> getCommentsByPostId(UUID postId) {
        return commentPersistencePort.findAllByPostId(postId)
                .stream()
                .map(commentMapper::toResponse)
                .toList();
    }

}
