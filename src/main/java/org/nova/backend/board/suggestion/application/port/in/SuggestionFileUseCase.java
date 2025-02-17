package org.nova.backend.board.suggestion.application.port.in;

import jakarta.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.UUID;
import org.nova.backend.board.suggestion.application.dto.response.SuggestionFileResponse;
import org.nova.backend.board.suggestion.domain.model.entity.SuggestionFile;
import org.springframework.web.multipart.MultipartFile;

public interface SuggestionFileUseCase {
    void downloadFile(UUID fileId, HttpServletResponse response, UUID memberId);
    List<SuggestionFile> findFilesByIds(List<UUID> fileIds);
    List<SuggestionFileResponse> uploadFiles(List<MultipartFile> files, UUID memberId);
}
