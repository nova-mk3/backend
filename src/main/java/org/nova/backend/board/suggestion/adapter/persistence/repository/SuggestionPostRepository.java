package org.nova.backend.board.suggestion.adapter.persistence.repository;

import java.util.UUID;
import org.nova.backend.board.suggestion.domain.model.entity.SuggestionPost;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SuggestionPostRepository extends JpaRepository<SuggestionPost, UUID> {
    @Query("SELECT p FROM SuggestionPost p WHERE LOWER(p.title) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<SuggestionPost> searchByTitle(
            @Param("keyword") String keyword,
            Pageable pageable
    );
}
