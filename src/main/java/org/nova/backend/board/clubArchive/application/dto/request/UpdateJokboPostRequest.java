package org.nova.backend.board.clubArchive.application.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.nova.backend.board.clubArchive.domain.model.valueobject.Semester;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateJokboPostRequest {
    @Schema(example = "[2020] [인공지능] 이건명 - 1학기")
    private String title;
    @Schema(example = "년도를 수정합니다.")
    private String content;
    @Schema(example = "2020")
    private int year;
    @Schema(example = "인공지능")
    private String subject;
    private Semester semester;
    @Schema(example = "이건명")
    private String professorName;
    private List<UUID> fileIds;
    private List<UUID> deleteFileIds;
}
