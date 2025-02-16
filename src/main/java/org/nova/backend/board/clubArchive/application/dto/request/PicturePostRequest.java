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
public class PicturePostRequest {
    @Schema(example = "2024 노바 정모 다녀왔습니다~~!")
    private String title;
    @Schema(example = "즐거운 하루였어요!")
    private String content;
    private List<UUID> imageFileIds;
}
