package org.nova.backend.board.clubArchive.application.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UpdatePicturePostRequest {
    @Schema(example = "년도가 잘못되었네요!")
    private String title;
    @Schema(example = "수정된 게시글입니다.")
    private String content;
    private List<UUID> imageFileIds;
    private List<UUID> deleteImageFileIds;
}