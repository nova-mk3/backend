package org.nova.backend.board.common.application.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateCommentRequest {
    @Schema(example = "수정된 댓글입니다!")
    private String content;
}
