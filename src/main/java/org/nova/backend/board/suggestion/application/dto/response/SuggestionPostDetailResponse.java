package org.nova.backend.board.suggestion.application.dto.response;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SuggestionPostDetailResponse {
    private UUID id;
    private String title;
    private String content;
    private LocalDateTime createdTime;
    private LocalDateTime modifiedTime;
    private boolean isPrivate;
    private String adminReply;
    private List<SuggestionFileResponse> files;
    private String authorName;
}