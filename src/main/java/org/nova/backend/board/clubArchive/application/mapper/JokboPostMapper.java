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
import org.nova.backend.member.application.dto.response.ProfilePhotoResponse;
import org.nova.backend.member.application.mapper.MemberProfilePhotoMapper;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class JokboPostMapper {

    private MemberProfilePhotoMapper profilePhotoMapper;

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

    public JokboPostDetailResponse toDetailResponseFromPost(Post post, boolean isLiked) {
        List<FileResponse> fileResponses = post.getFiles().stream()
                .map(file -> new FileResponse(
                        file.getId(),
                        file.getOriginalFilename(),
                        "/api/v1/files/" + file.getId() + "/download"
                ))
                .toList();

        ProfilePhotoResponse profilePhotoResponse = profilePhotoMapper.toResponse(post.getMember().getProfilePhoto());

        return new JokboPostDetailResponse(
                post.getId(),
                post.getTitle(),
                post.getContent(),
                post.getViewCount(),
                post.getLikeCount(),
                post.getCreatedTime(),
                post.getModifiedTime(),
                fileResponses,
                post.getMember().getName(),
                profilePhotoResponse,
                isLiked
        );
    }

    public JokboPostDetailResponse toDetailResponse(JokboPost post, boolean isLiked) {
        List<FileResponse> fileResponses = post.getPost().getFiles().stream()
                .map(file -> new FileResponse(
                        file.getId(),
                        file.getOriginalFilename(),
                        "/api/v1/files/" + file.getId() + "/download"
                ))
                .toList();

        ProfilePhotoResponse profilePhotoResponse = profilePhotoMapper.toResponse(
                post.getPost().getMember().getProfilePhoto());

        return new JokboPostDetailResponse(
                post.getPost().getId(),
                post.getPost().getTitle(),
                post.getPost().getContent(),
                post.getPost().getViewCount(),
                post.getPost().getLikeCount(),
                post.getPost().getCreatedTime(),
                post.getPost().getModifiedTime(),
                fileResponses,
                post.getPost().getMember().getName(),
                profilePhotoResponse,
                isLiked
        );
    }

    public JokboPostSummaryResponse toSummaryResponse(JokboPost post) {
        return new JokboPostSummaryResponse(
                post.getPost().getId(),
                post.getPost().getTitle(),
                post.getPost().getContent(),
                post.getPost().getViewCount(),
                post.getPost().getLikeCount(),
                post.getPost().getCreatedTime(),
                post.getPost().getModifiedTime(),
                post.getPost().getMember().getName(),
                post.getPost().getTotalDownloadCount(),
                post.getPost().getFiles().size()
        );
    }
}
