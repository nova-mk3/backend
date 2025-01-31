package org.nova.backend.board.application.port.in;

import java.util.List;
import java.util.UUID;
import org.nova.backend.board.application.dto.request.BasePostRequest;
import org.nova.backend.board.application.dto.response.PostResponse;
import org.nova.backend.board.domain.model.entity.Post;
import org.nova.backend.member.domain.model.entity.Member;
import org.springframework.web.multipart.MultipartFile;

//여기 반환갑 객체 그대로 주면 안됨. 유연하게 처리
public interface PostUseCase {
    PostResponse createPost(BasePostRequest request, Member member, List<MultipartFile> files);
    Post getPostById(UUID postId);
    void deletePost(UUID postId, UUID memberId);
}