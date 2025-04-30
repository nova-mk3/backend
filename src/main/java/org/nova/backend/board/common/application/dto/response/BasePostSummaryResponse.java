package org.nova.backend.board.common.application.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.nova.backend.board.common.domain.model.valueobject.PostType;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BasePostSummaryResponse {
    private UUID id;
    private PostType type;
    private String title;
    private String content;
    private int viewCount;
    private int likeCount;
    private int commentCount;
    private LocalDateTime createdTime;
    private LocalDateTime modifiedTime;
    private String authorName;
}
