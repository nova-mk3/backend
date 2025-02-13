package org.nova.backend.board.persistence;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.nova.backend.board.persistence.repository.FileRepository;
import org.nova.backend.board.common.application.port.out.FilePersistencePort;
import org.nova.backend.board.common.domain.model.entity.File;
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
    public File save(File file) {
        return fileRepository.save(file);
    }

    @Override
    public Optional<File> findFileById(UUID fileId) {
        return fileRepository.findById(fileId);
    }

    @Override
    public void deleteFileById(UUID fileId) {fileRepository.deleteById(fileId);}

}
