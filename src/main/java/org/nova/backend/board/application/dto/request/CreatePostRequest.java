package org.nova.backend.board.application.dto.request;

import java.util.List;
import lombok.Getter;
import org.nova.backend.board.domain.model.valueobject.PostType;
import org.springframework.web.multipart.MultipartFile;

@Getter
public class CreatePostRequest {
    private String title;
    private String content;
    private PostType dtype;
    private List<MultipartFile> files;
}
