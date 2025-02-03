package org.nova.backend.board.adapter.persistence;

import java.util.List;
import java.util.UUID;
import org.nova.backend.board.adapter.persistence.repository.FileRepository;
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
    public void deleteFilesByIds(List<UUID> fileIds) {
        fileRepository.deleteAllById(fileIds);
    }

    @Override
    public List<File> findFilesByIds(List<UUID> fileIds) {
        return fileRepository.findAllById(fileIds);
    }

    @Override
    public void save(File file) {
        fileRepository.save(file);
    }
}
