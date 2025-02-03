package org.nova.backend.board.application.mapper;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.nova.backend.board.application.dto.request.BasePostRequest;
import org.nova.backend.board.application.dto.response.FileResponse;
import org.nova.backend.board.application.dto.response.PostResponse;
import org.nova.backend.board.domain.model.entity.Board;
import org.nova.backend.board.domain.model.entity.Post;
import org.nova.backend.member.domain.model.entity.Member;
import org.springframework.stereotype.Component;

@Component
public class BasePostMapper {

    public Post toEntity(
            BasePostRequest request,
            Member member,
            Board board
    ) {
        return new Post(
                UUID.randomUUID(),
                member,
                board,
                request.getPostType(),
                request.getTitle(),
                request.getContent(),
                0,
                0,
                0,
                new ArrayList<>(),
                new ArrayList<>(),
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }

    public PostResponse toResponse(Post post) {
        List<FileResponse> fileResponses = post.getFiles().stream()
                .map(file -> new FileResponse(file.getId(), file.getOriginalFilename(), file.getFilePath()))
                .distinct()
                .toList();
        return new PostResponse(
                post.getId(),
                post.getTitle(),
                post.getContent(),
                post.getViewCount(),
                post.getLikeCount(),
                post.getCreatedTime(),
                fileResponses
        );
    }
}

