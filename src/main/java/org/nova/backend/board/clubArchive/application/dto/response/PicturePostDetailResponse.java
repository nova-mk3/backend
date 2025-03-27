package org.nova.backend.board.clubArchive.application.dto.response;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PicturePostDetailResponse {
    private UUID id;
    private String title;
    private String content;
    private int viewCount;
    private int likeCount;
    private int commentCount;
    private LocalDateTime createdTime;
    private LocalDateTime modifiedTime;
    private UUID authorId;
    private String authorName;
    private List<ImageResponse> images;
    private boolean isLiked;
}
