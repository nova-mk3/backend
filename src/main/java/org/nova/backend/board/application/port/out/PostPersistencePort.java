package org.nova.backend.board.application.port.out;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.nova.backend.board.domain.model.entity.Post;

public interface PostPersistencePort {
    List<Post> findAllPosts();
    Post save(Post post);
    Optional<Post> findById(UUID postId);
}
