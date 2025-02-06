package org.nova.backend.board.application.port.in;

import java.util.List;
import java.util.UUID;
import org.nova.backend.board.application.dto.request.CommentRequest;
import org.nova.backend.board.application.dto.request.UpdateCommentRequest;
import org.nova.backend.board.application.dto.response.CommentResponse;

public interface CommentUseCase {
    CommentResponse addComment(UUID postId, CommentRequest commentRequest, UUID memberId);
    CommentResponse updateComment(UUID commentId, UpdateCommentRequest updateCommentRequest, UUID memberId);
    List<CommentResponse> getCommentsByPostId(UUID postId);
    void deleteComment(UUID commentId, UUID memberId);
}
