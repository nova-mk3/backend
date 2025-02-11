package org.nova.backend.board.common.application.dto.response;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BasePostResponse {
    private UUID id;
    private String title;
    private String content;
    private int viewCount;
    private int likeCount;
    private LocalDateTime createdTime;
    private List<FileResponse> files;
}
