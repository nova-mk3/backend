package org.nova.backend.board.application.mapper;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.UUID;
import org.nova.backend.board.application.dto.request.CreatePostRequest;
import org.nova.backend.board.application.dto.response.PostResponse;
import org.nova.backend.board.domain.model.entity.Post;
import org.springframework.stereotype.Component;

@Component
public class PostMapper {

    public Post toEntity(CreatePostRequest request) {
        return new Post(
                UUID.randomUUID(),
                null,
                request.getDtype(),
                request.getTitle(),
                request.getContent(),
                0,
                0,
                0,
                new ArrayList<>(),
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }

    public PostResponse toResponse(Post post) {
        return new PostResponse(
                post.getPostId(),
                post.getTitle(),
                post.getContent(),
                post.getViewCount(),
                post.getLikeCount(),
                post.getCreatedTime()
        );
    }
}

