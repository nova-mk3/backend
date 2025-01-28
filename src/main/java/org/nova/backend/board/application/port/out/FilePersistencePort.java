package org.nova.backend.board.application.port.out;

import org.nova.backend.board.domain.model.entity.File;

public interface FilePersistencePort {
    void save(File file);
}
