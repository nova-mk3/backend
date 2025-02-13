package org.nova.backend.board.common.application.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateBasePostRequest {
    @Schema(example = "수정된 게시글입니다.")
    private String title;
    @Schema(example = "수정되었습니다!")
    private String content;
    private List<UUID> fileIds;
    private List<UUID> deleteFileIds;
}
