package org.nova.backend.board.common.application.mapper;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.nova.backend.board.common.application.dto.request.BasePostRequest;
import org.nova.backend.board.common.application.dto.response.FileResponse;
import org.nova.backend.board.common.application.dto.response.PostDetailResponse;
import org.nova.backend.board.common.application.dto.response.PostSummaryResponse;
import org.nova.backend.board.common.domain.model.entity.Board;
import org.nova.backend.board.common.domain.model.entity.Post;
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

    public PostDetailResponse toDetailResponse(Post post) {
        List<FileResponse> fileResponses = post.getFiles().stream()
                .map(file -> new FileResponse(
                        file.getId(),
                        file.getOriginalFilename(),
                        "/api/v1/files/" + file.getId() + "/download"
                ))
                .toList();
        return new PostDetailResponse(
                post.getId(),
                post.getTitle(),
                post.getContent(),
                post.getViewCount(),
                post.getLikeCount(),
                post.getCommentCount(),
                post.getCreatedTime(),
                post.getModifiedTime(),
                fileResponses,
                post.getMember().getName(),
                post.getMember().getProfilePhoto()
        );
    }

    public PostSummaryResponse toSummaryResponse(Post post) {
        return new PostSummaryResponse(
                post.getId(),
                post.getPostType(),
                post.getTitle(),
                post.getContent(),
                post.getViewCount(),
                post.getLikeCount(),
                post.getCreatedTime(),
                post.getModifiedTime(),
                post.getMember().getName(),
                post.getMember().getProfilePhoto()
        );
    }
}

