package org.nova.backend.board.common.application.port.in;

import java.util.List;
import java.util.UUID;
import org.nova.backend.board.common.application.dto.request.BasePostRequest;
import org.nova.backend.board.common.application.dto.request.UpdatePostRequest;
import org.nova.backend.board.common.application.dto.response.PostDetailResponse;
import org.nova.backend.board.common.application.dto.response.PostResponse;
import org.nova.backend.board.common.application.dto.response.PostSummaryResponse;
import org.nova.backend.board.common.domain.model.valueobject.PostType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

public interface PostUseCase {
    PostResponse createPost(UUID boardId, BasePostRequest request, UUID memberId, List<MultipartFile> files);
    Page<PostSummaryResponse> getPostsByCategory(UUID boardId, PostType postType, Pageable pageable);
    PostDetailResponse getPostById(UUID boardId, UUID postId);
    void updatePost(UUID boardId, UUID postId, UpdatePostRequest request, UUID memberId, List<MultipartFile> files);
    void deletePost(UUID boardId, UUID postId, UUID memberId);

    int likePost(UUID postId, UUID memberId);
    int unlikePost(UUID postId, UUID memberId);
}