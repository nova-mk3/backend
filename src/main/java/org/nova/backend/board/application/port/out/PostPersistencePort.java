package org.nova.backend.board.application.port.out;

import java.util.Optional;
import java.util.UUID;
import org.nova.backend.board.domain.model.entity.Post;
import org.nova.backend.board.domain.model.valueobject.PostType;
import org.nova.backend.member.domain.model.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.repository.query.Param;

public interface PostPersistencePort {
    Page<Post> findAllByBoardAndCategory(UUID boardId, PostType postType, Pageable pageable);
    Post save(Post post);
    @EntityGraph(attributePaths = {"files"})
    Optional<Post> findById(UUID postId);
    void deleteById(UUID postId);
    void increaseViewCount(@Param("postId") UUID postId);
    int likePost(@Param("postId") UUID postId, Member member);
    int unlikePost(@Param("postId") UUID postId, Member member);

}
