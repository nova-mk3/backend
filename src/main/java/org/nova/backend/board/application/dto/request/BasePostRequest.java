package org.nova.backend.board.application.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.nova.backend.board.domain.model.valueobject.PostType;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BasePostRequest {
    private String title;
    private String content;
    private PostType postType;
}
