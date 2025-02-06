package org.nova.backend.board.common.application.service;

import jakarta.transaction.Transactional;
import java.util.List;
import java.util.UUID;
import org.nova.backend.board.common.application.dto.request.CommentRequest;
import org.nova.backend.board.common.application.dto.request.UpdateCommentRequest;
import org.nova.backend.board.common.application.dto.response.CommentResponse;
import org.nova.backend.board.common.application.mapper.CommentMapper;
import org.nova.backend.board.common.application.port.in.CommentUseCase;
import org.nova.backend.board.common.application.port.out.CommentPersistencePort;
import org.nova.backend.board.common.application.port.out.PostPersistencePort;
import org.nova.backend.board.common.domain.exception.BoardDomainException;
import org.nova.backend.board.common.domain.exception.CommentDomainException;
import org.nova.backend.board.common.domain.model.entity.Comment;
import org.nova.backend.board.common.domain.model.entity.Post;
import org.nova.backend.member.adapter.repository.MemberRepository;
import org.nova.backend.member.domain.model.entity.Member;
import org.nova.backend.member.domain.model.valueobject.Role;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class CommentService implements CommentUseCase {
    private static final Logger logger = LoggerFactory.getLogger(CommentService.class);

    private final CommentPersistencePort commentPersistencePort;
    private final PostPersistencePort postPersistencePort;
    private final MemberRepository memberRepository;
    private final CommentMapper commentMapper;

    public CommentService(
            CommentPersistencePort commentPersistencePort,
            PostPersistencePort postPersistencePort,
            MemberRepository memberRepository,
            CommentMapper commentMapper
    ) {
        this.commentPersistencePort = commentPersistencePort;
        this.postPersistencePort = postPersistencePort;
        this.memberRepository = memberRepository;
        this.commentMapper = commentMapper;
    }

    /**
     * 댓글 수정 (본인 댓글만 수정 가능)
     */
    @Override
    @Transactional
    public CommentResponse updateComment(
            UUID commentId,
            UpdateCommentRequest request,
            UUID memberId
    ) {
        Comment comment = commentPersistencePort.findById(commentId)
                .orElseThrow(() -> new CommentDomainException("댓글을 찾을 수 없습니다."));

        if (!comment.getMember().getId().equals(memberId)) {
            throw new CommentDomainException("자신의 댓글만 수정할 수 있습니다.");
        }

        comment.updateContent(request.getContent());
        commentPersistencePort.save(comment);

        return commentMapper.toResponse(comment, List.of(comment));
    }

    /**
     * 댓글 삭제 (작성자 본인과 관리자만 삭제 가능)
     */
    @Override
    @Transactional
    public void deleteComment(
            UUID commentId,
            UUID memberId
    ) {
        Comment comment = commentPersistencePort.findById(commentId)
                .orElseThrow(() -> new CommentDomainException("댓글을 찾을 수 없습니다."));

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BoardDomainException("사용자를 찾을 수 없습니다."));

        if (!comment.getMember().getId().equals(memberId) && member.getRole() != Role.ADMINISTRATOR) {
            logger.warn("사용자 {}가 댓글 {}를 삭제하려 했으나 권한이 없습니다.", memberId, commentId);
            throw new CommentDomainException("댓글 삭제 권한이 없습니다.");
        }

        commentPersistencePort.deleteById(commentId);
    }


    /**
     * 댓글 작성 (대댓글 포함)
     */
    @Override
    @Transactional
    public CommentResponse addComment(
            UUID postId,
            CommentRequest request,
            UUID memberId
    ) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BoardDomainException("사용자를 찾을 수 없습니다."));

        Post post = postPersistencePort.findById(postId)
                .orElseThrow(() -> new BoardDomainException("게시글을 찾을 수 없습니다."));

        Comment parentComment = null;
        if (request.getParentCommentId() != null) {
            parentComment = commentPersistencePort.findById(request.getParentCommentId())
                    .orElseThrow(() -> new CommentDomainException("대댓글을 찾을 수 없습니다."));
        }

        Comment comment = commentMapper.toEntity(request, post, member, parentComment);
        comment = commentPersistencePort.save(comment);

        post.incrementCommentCount();
        postPersistencePort.save(post);

        List<Comment> allComments = commentPersistencePort.findAllByPostId(postId);

        return commentMapper.toResponse(comment, allComments);
    }


    /**
     * 특정 게시글의 댓글 조회
     */
    @Override
    @Transactional
    public List<CommentResponse> getCommentsByPostId(UUID postId) {
        List<Comment> allComments = commentPersistencePort.findAllByPostId(postId);
        return commentMapper.toResponseList(allComments);
    }
}
