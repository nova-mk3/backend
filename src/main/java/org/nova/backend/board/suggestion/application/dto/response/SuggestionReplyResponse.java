package org.nova.backend.board.suggestion.application.dto.response;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SuggestionReplyResponse {
    private String content;
    private LocalDateTime adminReplyTime;
}
