package org.nova.backend.board.common.application.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.nova.backend.board.common.domain.model.valueobject.PostType;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BasePostRequest {
    @Schema(example = "게시글입니다.")
    private String title;
    @Schema(example = "모두 안녕하세요")
    private String content;
    private PostType postType;
    private List<UUID> fileIds;
}
