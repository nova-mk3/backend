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
public class AllPostSummaryResponse {
    private UUID id;
    private PostType postType;
    private String title;
    private int viewCount;
    private LocalDateTime createdTime;
    private LocalDateTime modifiedTime;
}
