package org.nova.backend.board.persistence.repository;

import java.util.List;
import java.util.UUID;
import org.nova.backend.board.suggestion.domain.model.entity.SuggestionFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SuggestionFileRepository extends JpaRepository<SuggestionFile, UUID> {
    List<SuggestionFile> findBySuggestionPostId(UUID postId);
}
