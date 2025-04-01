package org.nova.backend.board.clubArchive.application.mapper;

import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.nova.backend.board.clubArchive.application.dto.response.ImageResponse;
import org.nova.backend.board.clubArchive.application.dto.response.PicturePostSummaryResponse;
import org.nova.backend.board.clubArchive.application.dto.response.PicturePostDetailResponse;
import org.nova.backend.board.clubArchive.application.service.ImageFileService;
import org.nova.backend.board.common.domain.model.entity.File;
import org.nova.backend.board.common.domain.model.entity.Post;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PicturePostMapper {
    private final ImageFileService imageFileService;

    public PicturePostSummaryResponse toSummaryResponse(Post post) {
        List<UUID> fileIds = post.getFiles().stream().map(File::getId).toList();
        ImageResponse thumbnail = imageFileService.getThumbnail(fileIds);

        return new PicturePostSummaryResponse(
                post.getId(),
                post.getTitle(),
                post.getViewCount(),
                post.getLikeCount(),
                post.getCreatedTime(),
                post.getModifiedTime(),
                post.getMember().getName(),
                post.getFiles().size(),
                thumbnail != null ? thumbnail.getId() : null,
                thumbnail != null ? thumbnail.getOriginalFileName() : null,
                thumbnail != null ? thumbnail.getImageUrl() : null,
                thumbnail != null ? thumbnail.getWidth() : 0,
                thumbnail != null ? thumbnail.getHeight() : 0
        );
    }

    public PicturePostDetailResponse toDetailResponse(
            Post post,
            boolean isLiked
    ) {
        List<ImageResponse> images = post.getFiles().stream()
                .map(imageFileService::createImageResponse)
                .toList();

        return new PicturePostDetailResponse(
                post.getId(),
                post.getTitle(),
                post.getContent(),
                post.getViewCount(),
                post.getLikeCount(),
                post.getCommentCount(),
                post.getCreatedTime(),
                post.getModifiedTime(),
                post.getMember().getId(),
                post.getMember().getName(),
                images,
                isLiked
        );
    }
}
