package org.nova.backend.board.common.application.port.in;

import java.util.List;
import java.util.UUID;
import org.nova.backend.board.common.domain.model.entity.File;
import org.nova.backend.board.common.domain.model.entity.Post;
import org.springframework.web.multipart.MultipartFile;

public interface FileUseCase {
    List<File> saveFiles(List<MultipartFile> files, Post post);
    void deleteFiles(List<UUID> fileIds);
    List<File> findFilesByIds(List<UUID> fileIds);
}

