package org.nova.backend.board.application.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CommentResponse {
    private UUID id;
    private UUID postId;
    private UUID memberId;
    private UUID parentCommentId;
    private String content;
    private LocalDateTime createdTime;
    private LocalDateTime modifiedTime;
}
