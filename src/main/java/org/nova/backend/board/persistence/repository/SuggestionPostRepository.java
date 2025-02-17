package org.nova.backend.board.persistence.repository;

import java.util.UUID;
import org.nova.backend.board.suggestion.domain.model.entity.SuggestionPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SuggestionPostRepository extends JpaRepository<SuggestionPost, UUID> {
}
