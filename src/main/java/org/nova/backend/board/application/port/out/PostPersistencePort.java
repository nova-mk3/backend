package org.nova.backend.board.application.port.out;

import java.util.Optional;
import java.util.UUID;
import org.nova.backend.board.domain.model.entity.Post;
import org.nova.backend.board.domain.model.valueobject.BoardCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;

public interface PostPersistencePort {
    Page<Post> findAllByCategory(BoardCategory category, Pageable pageable);
    Post save(Post post);   // 게시글 저장
    Optional<Post> findById(UUID postId);   // 게시글 ID로 조회
    void deleteById(UUID postId);   // 게시글 ID로 삭제 // 게시글 삭제
    void increaseViewCount(@Param("postId") UUID postId);
}
