package org.nova.backend.board.application.dto.response;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class FileResponse {
    private UUID id;
    private String originalFileName;
    private String filePath;
}
