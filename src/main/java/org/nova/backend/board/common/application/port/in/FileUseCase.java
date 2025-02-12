package org.nova.backend.board.common.application.port.in;

import jakarta.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.UUID;
import org.nova.backend.board.common.domain.model.entity.File;
import org.springframework.web.multipart.MultipartFile;

public interface FileUseCase {
    void deleteFileById(UUID fileId, UUID memberId);
    void downloadFile(UUID fileId, HttpServletResponse response, UUID memberId);
    void deleteFiles(List<UUID> fileIds);
    List<File> findFilesByIds(List<UUID> fileIds);
    List<UUID> uploadFiles(List<MultipartFile> files, UUID memberId);
}

