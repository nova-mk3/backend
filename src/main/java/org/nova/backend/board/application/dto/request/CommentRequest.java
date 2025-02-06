package org.nova.backend.board.application.dto.request;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CommentRequest {
    private UUID parentCommentId;
    private String content;
}
