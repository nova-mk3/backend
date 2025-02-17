package org.nova.backend.board.suggestion.application.port.out;

import java.util.Optional;
import java.util.UUID;
import org.nova.backend.board.suggestion.domain.model.entity.SuggestionPost;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SuggestionPostPersistencePort {
    SuggestionPost save(SuggestionPost post);
    Page<SuggestionPost> findAll(Pageable pageable);
    Optional<SuggestionPost> findById(UUID postId);
}