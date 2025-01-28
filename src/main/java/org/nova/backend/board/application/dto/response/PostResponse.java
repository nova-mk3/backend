package org.nova.backend.board.application.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PostResponse {
    private UUID Id;
    private String title;
    private String content;
    private int viewCount;
    private int likeCount;
    private LocalDateTime createdTime;
}
