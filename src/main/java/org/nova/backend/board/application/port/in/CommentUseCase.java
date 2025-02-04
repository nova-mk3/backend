package org.nova.backend.board.application.port.in;

import java.util.List;
import java.util.UUID;
import org.nova.backend.board.application.dto.request.CommentRequest;
import org.nova.backend.board.application.dto.response.CommentResponse;

public interface CommentUseCase {
    CommentResponse addComment(UUID postId, CommentRequest commentRequest, UUID memberId);
    List<CommentResponse> getCommentsByPostId(UUID postId);
}
