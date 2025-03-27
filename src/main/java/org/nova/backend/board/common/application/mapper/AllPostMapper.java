package org.nova.backend.board.common.application.mapper;

import lombok.AllArgsConstructor;
import org.nova.backend.board.common.application.dto.response.AllPostSummaryResponse;
import org.nova.backend.board.common.domain.model.entity.Post;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class AllPostMapper {
    public AllPostSummaryResponse toSummaryResponse(Post post) {
        return new AllPostSummaryResponse(
                post.getId(),
                post.getPostType(),
                post.getTitle(),
                post.getViewCount(),
                post.getCreatedTime(),
                post.getModifiedTime()
        );
    }
}
