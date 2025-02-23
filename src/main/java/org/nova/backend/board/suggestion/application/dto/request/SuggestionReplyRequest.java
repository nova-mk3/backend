package org.nova.backend.board.suggestion.application.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SuggestionReplyRequest {
    @Schema(example = "죄송합니다...")
    private String reply;
}
