package org.nova.backend.board.suggestion.adapter.persistence.repository;

import java.util.Optional;
import java.util.UUID;
import org.nova.backend.board.suggestion.domain.model.entity.SuggestionFile;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SuggestionFileRepository extends JpaRepository<SuggestionFile, UUID> {
    @EntityGraph(attributePaths = {"suggestionPost"})
    Optional<SuggestionFile> findById(UUID id);
}
