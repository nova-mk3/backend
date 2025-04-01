package org.nova.backend.board.clubArchive.application.mapper;

import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.nova.backend.board.clubArchive.application.dto.request.JokboPostRequest;
import org.nova.backend.board.clubArchive.application.dto.response.JokboPostDetailResponse;
import org.nova.backend.board.clubArchive.application.dto.response.JokboPostSummaryResponse;
import org.nova.backend.board.clubArchive.domain.model.entity.JokboPost;
import org.nova.backend.board.common.application.dto.response.FileResponse;
import org.nova.backend.board.common.domain.model.entity.Post;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class JokboPostMapper {

    public JokboPost toEntity(
            JokboPostRequest request,
            Post savedPost
    ) {
        return new JokboPost(
                UUID.randomUUID(),
                savedPost,
                request.getProfessorName(),
                request.getYear(),
                request.getSemester(),
                request.getSubject()
        );
    }

    public JokboPostDetailResponse toDetailResponseFromPost(
            JokboPost jokboPost,
            Post post,
            boolean isLiked
    ) {
        List<FileResponse> fileResponses = post.getFiles().stream()
                .map(file -> new FileResponse(
                        file.getId(),
                        file.getOriginalFilename(),
                        "/api/v1/files/" + file.getId() + "/download"
                ))
                .toList();

        return new JokboPostDetailResponse(
                post.getId(),
                post.getTitle(),
                post.getContent(),
                jokboPost.getYear(),
                jokboPost.getSubject(),
                jokboPost.getSemester(),
                jokboPost.getProfessorName(),
                post.getViewCount(),
                post.getLikeCount(),
                post.getCommentCount(),
                post.getCreatedTime(),
                post.getModifiedTime(),
                fileResponses,
                post.getMember().getId(),
                post.getMember().getName(),
                isLiked
        );
    }

    public JokboPostDetailResponse toDetailResponse(
            JokboPost jokboPost,
            boolean isLiked
    ) {
        Post post = jokboPost.getPost();

        List<FileResponse> fileResponses = jokboPost.getPost().getFiles().stream()
                .map(file -> new FileResponse(
                        file.getId(),
                        file.getOriginalFilename(),
                        "/api/v1/files/" + file.getId() + "/download"
                ))
                .toList();

        return new JokboPostDetailResponse(
                post.getId(),
                post.getTitle(),
                post.getContent(),
                jokboPost.getYear(),
                jokboPost.getSubject(),
                jokboPost.getSemester(),
                jokboPost.getProfessorName(),
                post.getViewCount(),
                post.getLikeCount(),
                post.getCommentCount(),
                post.getCreatedTime(),
                post.getModifiedTime(),
                fileResponses,
                post.getMember().getId(),
                post.getMember().getName(),
                isLiked
        );
    }

    public JokboPostSummaryResponse toSummaryResponse(JokboPost jokboPost) {
        Post post = jokboPost.getPost();

        return new JokboPostSummaryResponse(
                post.getId(),
                post.getTitle(),
                post.getContent(),
                post.getViewCount(),
                post.getLikeCount(),
                post.getCreatedTime(),
                post.getModifiedTime(),
                post.getMember().getName(),
                post.getTotalDownloadCount(),
                post.getFiles().size()
        );
    }
}
