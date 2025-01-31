package org.nova.backend.board.application.port.in;

import java.util.List;
import java.util.UUID;
import org.nova.backend.board.application.dto.request.CommentRequest;
import org.nova.backend.board.application.dto.response.CommentResponse;
import org.nova.backend.member.domain.model.entity.Member;

public interface CommentUseCase {
    CommentResponse addComment(CommentRequest commentRequest, Member member);
    List<CommentResponse> getCommentsByPostId(UUID postId);
}
