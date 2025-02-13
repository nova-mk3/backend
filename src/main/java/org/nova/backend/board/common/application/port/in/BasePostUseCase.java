package org.nova.backend.board.common.application.port.in;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.nova.backend.board.common.application.dto.request.BasePostRequest;
import org.nova.backend.board.common.application.dto.request.UpdateBasePostRequest;
import org.nova.backend.board.common.application.dto.response.BasePostDetailResponse;
import org.nova.backend.board.common.application.dto.response.BasePostSummaryResponse;
import org.nova.backend.board.common.domain.model.valueobject.PostType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BasePostUseCase {
    BasePostDetailResponse createPost(UUID boardId, BasePostRequest request, UUID memberId);
    Page<BasePostSummaryResponse> getPostsByCategory(UUID boardId, PostType postType, Pageable pageable);
    Page<BasePostSummaryResponse> getAllPosts(UUID boardId, Pageable pageable);

    BasePostDetailResponse getPostById(UUID boardId, UUID postId);
    Map<PostType, List<BasePostSummaryResponse>> getLatestPostsByType(UUID boardId);

    void updatePost(UUID boardId, UUID postId, UpdateBasePostRequest request, UUID memberId);
    void deletePost(UUID boardId, UUID postId, UUID memberId);

    int likePost(UUID postId, UUID memberId);
    int unlikePost(UUID postId, UUID memberId);
}