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
    private String authorName;
    private String title;
    private LocalDateTime createdTime;
    private boolean isPrivate;
    private boolean isAnswered;
    private boolean isAdminRead;
    private boolean isAuthor;
}