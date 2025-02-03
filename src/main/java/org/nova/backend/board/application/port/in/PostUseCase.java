package org.nova.backend.board.application.port.in;

import java.util.List;
import java.util.UUID;
import org.nova.backend.board.application.dto.request.BasePostRequest;
import org.nova.backend.board.application.dto.request.UpdatePostRequest;
import org.nova.backend.board.application.dto.response.PostResponse;
import org.nova.backend.board.domain.model.valueobject.BoardCategory;
import org.nova.backend.member.domain.model.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

public interface PostUseCase {
    PostResponse createPost(UUID boardId, BasePostRequest request, UUID memberId, List<MultipartFile> files);
    Page<PostResponse> getPostsByCategory(BoardCategory category, Pageable pageable);
    PostResponse getPostById(UUID postId);
    void updatePost(UUID boardId, UUID postId, UpdatePostRequest request, UUID memberId, List<MultipartFile> files);
    void deletePost(UUID boardId, UUID postId, UUID memberId);

    int likePost(UUID postId, Member member);
    int unlikePost(UUID postId, Member member);
}