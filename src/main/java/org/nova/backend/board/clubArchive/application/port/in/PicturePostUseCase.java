package org.nova.backend.board.clubArchive.application.port.in;

import java.util.UUID;
import org.nova.backend.board.clubArchive.application.dto.request.PicturePostRequest;
import org.nova.backend.board.clubArchive.application.dto.request.UpdatePicturePostRequest;
import org.nova.backend.board.clubArchive.application.dto.response.PicturePostDetailResponse;

public interface PicturePostUseCase {
    PicturePostDetailResponse createPost(UUID boardId, PicturePostRequest request, UUID memberId);
    PicturePostDetailResponse updatePost(UUID boardId, UUID postId, UpdatePicturePostRequest request, UUID memberId);
    void deletePost(UUID boardId, UUID postId, UUID memberId);
    PicturePostDetailResponse getPostById(UUID boardId, UUID postId);
}
