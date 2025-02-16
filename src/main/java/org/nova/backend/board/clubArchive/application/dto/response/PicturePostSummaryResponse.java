package org.nova.backend.board.clubArchive.application.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PicturePostSummaryResponse {
    private UUID id;
    private String title;
    private int viewCount;
    private int likeCount;
    private LocalDateTime createdTime;
    private LocalDateTime modifiedTime;
    private String authorName;
    private int totalFileDownloadCount;
    private UUID thumbnailId;
    private String thumbnailUrl;
    private int thumbnailWidth;
    private int thumbnailHeight;
}