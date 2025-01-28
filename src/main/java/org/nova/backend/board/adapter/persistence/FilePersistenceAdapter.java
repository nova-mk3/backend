package org.nova.backend.board.adapter.persistence;

import org.nova.backend.board.adapter.repository.FileRepository;
import org.nova.backend.board.application.port.out.FilePersistencePort;
import org.nova.backend.board.domain.model.entity.File;
import org.springframework.stereotype.Component;

@Component
public class FilePersistenceAdapter implements FilePersistencePort {
    private final FileRepository fileRepository;

    public FilePersistenceAdapter(FileRepository fileRepository) {
        this.fileRepository = fileRepository;
    }

    @Override
    public void save(File file) {
        fileRepository.save(file);
    }
}
