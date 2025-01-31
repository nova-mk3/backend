package org.nova.backend.board.application.port.out;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.nova.backend.board.domain.model.entity.Post;

public interface PostPersistencePort {
    List<Post> findAllPosts();   // 모든 게시글 조회
    Post save(Post post);   // 게시글 저장
    Optional<Post> findById(UUID postId);   // 게시글 ID로 조회
    void deleteById(UUID postId);   // 게시글 ID로 삭제 // 게시글 삭제
}
