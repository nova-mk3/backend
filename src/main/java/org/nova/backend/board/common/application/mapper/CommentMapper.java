package org.nova.backend.board.common.application.mapper;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import org.nova.backend.board.common.application.dto.request.CommentRequest;
import org.nova.backend.board.common.application.dto.response.CommentResponse;
import org.nova.backend.board.common.domain.model.entity.Comment;
import org.nova.backend.board.common.domain.model.entity.Post;
import org.nova.backend.member.domain.model.entity.Member;
import org.springframework.stereotype.Component;

@Component
public class CommentMapper {

    public Comment toEntity(
            CommentRequest request,
            Post post,
            Member member,
            Comment parentComment
    ) {
        return new Comment(
                null,
                post,
                member,
                parentComment,
                request.getContent(),
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }

    /**
     * 특정 댓글을 CommentResponse로 변환
     */
    public CommentResponse toResponse(Comment comment, List<Comment> allComments) {

        List<CommentResponse> childComment = allComments.stream()
                .filter(c -> c.getParentComment() != null && c.getParentComment().getId().equals(comment.getId()))
                .sorted(Comparator.comparing(Comment::getCreatedTime))
                .map(c -> toResponse(c, allComments))
                .toList();

        return new CommentResponse(
                comment.getId(),
                comment.getContent(),
                comment.getCreatedTime(),
                comment.getModifiedTime(),
                comment.getMember().getName(),
                comment.getMember().getProfilePhoto(),
                childComment
        );
    }

    /**
     * 부모 댓글 리스트 변환
     */
    public List<CommentResponse> toResponseList(List<Comment> comments) {
        return comments.stream()
                .filter(comment -> comment.getParentComment() == null)
                .map(comment -> toResponse(comment, comments))
                .toList();
    }
}
