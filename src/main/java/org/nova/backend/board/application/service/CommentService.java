package org.nova.backend.board.application.service;

import jakarta.transaction.Transactional;
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
import org.nova.backend.member.adapter.repository.MemberRepository;
import org.nova.backend.member.domain.model.entity.Member;
import org.springframework.stereotype.Service;

@Service
public class CommentService implements CommentUseCase {
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
     * 댓글 작성 (대댓글 포함)
     */
    @Override
    @Transactional
    public CommentResponse addComment(
            UUID postId,
            CommentRequest request,
            UUID memberId
    ) {
        // 1. 작성자(Member) 조회
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BoardDomainException("사용자를 찾을 수 없습니다."));

        // 2. 게시글(Post) 조회
        Post post = postPersistencePort.findById(postId)
                .orElseThrow(() -> new BoardDomainException("게시글을 찾을 수 없습니다."));

        // 3. 부모 댓글 조회 (대댓글인 경우)
        Comment parentComment = null;
        if (request.getParentCommentId() != null) {
            parentComment = commentPersistencePort.findById(request.getParentCommentId())
                    .orElseThrow(() -> new CommentDomainException("대댓글을 찾을 수 없습니다."));
        }

        // 4. 댓글 엔티티 생성 및 저장
        Comment comment = commentMapper.toEntity(request, post, member, parentComment);
        comment = commentPersistencePort.save(comment);

        // 5. 게시글의 댓글 수 증가
        post.incrementCommentCount();
        postPersistencePort.save(post);

        // 6. DB에서 전체 댓글 리스트 조회 (트랜잭션 반영된 최신 데이터)
        List<Comment> allComments = commentPersistencePort.findAllByPostId(postId);

        // 7. 방금 추가한 댓글을 응답 객체로 변환하여 반환
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
