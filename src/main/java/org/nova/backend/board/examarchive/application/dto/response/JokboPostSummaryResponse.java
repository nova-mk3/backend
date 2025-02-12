package org.nova.backend.board.examarchive.application.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class JokboPostSummaryResponse {
    private UUID id;
    private String title;
    private String content;
    private int viewCount;
    private int likeCount;
    private LocalDateTime createdTime;
    private LocalDateTime modifiedTime;
    private String authorName;
    private int totalFileDownloadCount;
    private int fileContentCount;
}
