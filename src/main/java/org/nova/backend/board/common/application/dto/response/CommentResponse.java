package org.nova.backend.board.common.application.dto.response;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.nova.backend.member.application.dto.response.ProfilePhotoResponse;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CommentResponse {
    private UUID id;
    private String content;
    private LocalDateTime createdTime;
    private LocalDateTime modifiedTime;
    private UUID authorId;
    private String authorName;
    private ProfilePhotoResponse authorProfilePhoto;
    private List<CommentResponse> children;
}
