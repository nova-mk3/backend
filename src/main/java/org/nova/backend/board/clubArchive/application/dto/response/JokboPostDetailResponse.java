package org.nova.backend.board.clubArchive.application.dto.response;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.nova.backend.board.clubArchive.domain.model.valueobject.Semester;
import org.nova.backend.board.common.application.dto.response.FileResponse;
import org.nova.backend.member.application.dto.response.ProfilePhotoResponse;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class JokboPostDetailResponse {
    private UUID id;
    private String title;
    private String content;
    private int year;
    private String subject;
    private Semester semester;
    private String professorName;
    private int viewCount;
    private int likeCount;
    private LocalDateTime createdTime;
    private LocalDateTime modifiedTime;
    private List<FileResponse> files;
    private String authorName;
    private ProfilePhotoResponse authorProfilePhoto;
    private boolean isLiked;
}
