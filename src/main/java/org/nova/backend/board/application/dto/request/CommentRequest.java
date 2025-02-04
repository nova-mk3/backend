package org.nova.backend.board.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CommentRequest {
    private UUID parentCommentId;
    @NotBlank(message = "댓글 내용은 비어 있을 수 없습니다.")
    private String content;
}
