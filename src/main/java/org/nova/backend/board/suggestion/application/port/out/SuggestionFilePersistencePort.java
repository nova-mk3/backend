package org.nova.backend.board.suggestion.application.port.out;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.nova.backend.board.suggestion.domain.model.entity.SuggestionFile;

public interface SuggestionFilePersistencePort {
    SuggestionFile save(SuggestionFile file);
    List<SuggestionFile> findFilesByIds(List<UUID> fileIds);
    Optional<SuggestionFile> findFileById(UUID fileId);
}
