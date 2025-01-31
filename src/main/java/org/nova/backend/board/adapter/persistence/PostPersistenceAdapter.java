package org.nova.backend.board.adapter.persistence;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.nova.backend.board.adapter.persistence.repository.PostRepository;
import org.nova.backend.board.application.port.out.PostPersistencePort;
import org.nova.backend.board.domain.model.entity.Post;
import org.springframework.stereotype.Component;

@Component
public class PostPersistenceAdapter implements PostPersistencePort {
    private final PostRepository postRepository;

    public PostPersistenceAdapter(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    @Override
    public List<Post> findAllPosts() {
        return postRepository.findAll();
    }

    @Override
    public Post save(Post post) {
        return postRepository.save(post);
    }

    @Override
    public Optional<Post> findById(UUID postId) {
        return postRepository.findById(postId);
    }

    @Override
    public void deleteById(UUID postId) {
        postRepository.deleteById(postId);
    }
}
