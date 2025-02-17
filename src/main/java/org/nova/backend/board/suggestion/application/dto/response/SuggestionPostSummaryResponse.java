package org.nova.backend.board.suggestion.application.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SuggestionPostSummaryResponse {
    private UUID id;
    private String title;
    private LocalDateTime createdTime;
    private LocalDateTime modifiedTime;
    private boolean isPrivate;
    private boolean isAnswered;
    private boolean isAnswerRead;
    private boolean isAuthor;
}