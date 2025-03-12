package org.nova.backend.board.clubArchive.application.dto.response;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ImageResponse {
    private UUID id;
    private String originalFileName;
    private String imageUrl;
    private int width;
    private int height;
}
