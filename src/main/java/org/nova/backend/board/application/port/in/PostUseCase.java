package org.nova.backend.board.application.port.in;

import java.util.List;
import java.util.UUID;
import org.nova.backend.board.domain.model.entity.Post;

public interface PostUseCase {
    List<Post> getAllPosts();
    Post createPost(Post post);
    Post getPostById(UUID postId);
}
