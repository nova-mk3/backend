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
    PostResponse createPost(UUID boardId, BasePostRequest request, Member member, List<MultipartFile> files);
    Page<PostResponse> getPostsByCategory(BoardCategory category, Pageable pageable);
    PostResponse getPostById(UUID postId);
    PostResponse updatePost(UUID boardId, UUID postId, UpdatePostRequest request, Member member, List<MultipartFile> files);

    int likePost(UUID postId, Member member);
    int unlikePost(UUID postId, Member member);
    void deletePost(UUID postId, UUID memberId);
}