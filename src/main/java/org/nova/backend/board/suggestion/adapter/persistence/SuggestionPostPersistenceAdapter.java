package org.nova.backend.board.suggestion.adapter.persistence;

import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.nova.backend.board.suggestion.adapter.persistence.repository.SuggestionPostRepository;
import org.nova.backend.board.suggestion.application.port.out.SuggestionPostPersistencePort;
import org.nova.backend.board.suggestion.domain.model.entity.SuggestionPost;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SuggestionPostPersistenceAdapter implements SuggestionPostPersistencePort {
    private final SuggestionPostRepository postRepository;

    @Override
    public SuggestionPost save(SuggestionPost post) {
        return postRepository.save(post);
    }

    @Override
    public Page<SuggestionPost> findAll(Pageable pageable) {
        return postRepository.findAll(pageable);
    }

    @Override
    public Optional<SuggestionPost> findById(UUID postId) {
        return postRepository.findById(postId);
    }
}