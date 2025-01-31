package org.nova.backend.board.application.mapper;

import java.util.List;
import java.util.stream.Collectors;
import org.nova.backend.board.application.dto.response.CommentResponse;
import org.nova.backend.board.domain.model.entity.Comment;
import org.springframework.stereotype.Component;

@Component
public class CommentMapper {
    public CommentResponse toResponse(Comment comment) {
        return new CommentResponse(
                comment.getId(),
                comment.getPost().getId(),
                comment.getMember().getId(),
                comment.getParentComment() != null ? comment.getParentComment().getId() : null,
                comment.getContent(),
                comment.getCreatedTime(),
                comment.getModifiedTime()
        );
    }

    public List<CommentResponse> toResponseList(List<Comment> comments) {
        return comments.stream().map(this::toResponse).collect(Collectors.toList());
    }
}
