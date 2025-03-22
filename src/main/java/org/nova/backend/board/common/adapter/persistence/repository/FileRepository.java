package org.nova.backend.board.common.adapter.persistence.repository;

import java.util.Optional;
import java.util.UUID;
import org.nova.backend.board.common.domain.model.entity.File;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FileRepository extends JpaRepository<File, UUID> {
    @EntityGraph(attributePaths = {"post"})
    Optional<File> findById(UUID id);
}
