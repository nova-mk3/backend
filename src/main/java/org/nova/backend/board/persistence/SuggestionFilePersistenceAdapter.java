package org.nova.backend.board.persistence;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.nova.backend.board.persistence.repository.SuggestionFileRepository;
import org.nova.backend.board.suggestion.application.port.out.SuggestionFilePersistencePort;
import org.nova.backend.board.suggestion.domain.model.entity.SuggestionFile;
import org.springframework.stereotype.Component;

@Component
public class SuggestionFilePersistenceAdapter implements SuggestionFilePersistencePort {
    private final SuggestionFileRepository fileRepository;

    public SuggestionFilePersistenceAdapter(SuggestionFileRepository fileRepository) {
        this.fileRepository = fileRepository;
    }

    @Override
    public SuggestionFile save(SuggestionFile file) {
        return fileRepository.save(file);
    }

    @Override
    public List<SuggestionFile> findFilesByIds(List<UUID> fileIds) {
        return fileRepository.findAllById(fileIds);
    }

    @Override
    public Optional<SuggestionFile> findFileById(UUID fileId) {
        return fileRepository.findById(fileId);
    }
}
