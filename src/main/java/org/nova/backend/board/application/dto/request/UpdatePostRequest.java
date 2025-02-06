package org.nova.backend.board.application.dto.request;

import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UpdatePostRequest {
    private String title;
    private String content;
    private List<UUID> deleteFileIds;
}
