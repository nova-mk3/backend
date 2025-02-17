package org.nova.backend.board.suggestion.application.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SuggestionPostRequest {
    @Schema(example = "속도가 너무 느려요")
    private String title;
    @Schema(example = "백엔드 최적화좀 해주세요")
    private String content;
    private List<UUID> fileIds;
    private Boolean isPrivate;
}
