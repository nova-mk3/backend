package org.nova.backend.board.clubArchive.application.port.in;

import org.nova.backend.board.clubArchive.application.dto.response.JokboPostDetailResponse;
import org.nova.backend.board.clubArchive.application.dto.response.JokboPostSummaryResponse;
import org.nova.backend.board.clubArchive.application.dto.request.UpdateJokboPostRequest;
import org.nova.backend.board.clubArchive.application.dto.request.JokboPostRequest;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.nova.backend.board.clubArchive.domain.model.valueobject.Semester;

public interface JokboPostUseCase {
    JokboPostDetailResponse createPost(UUID boardId, JokboPostRequest request, UUID memberId);
    JokboPostDetailResponse updatePost(UUID boardId, UUID postId, UpdateJokboPostRequest request, UUID memberId);
    void deletePost(UUID boardId, UUID postId, UUID memberId);
    JokboPostDetailResponse getPostById(UUID boardId, UUID postId);
    Page<JokboPostSummaryResponse> getPostsByFilter(UUID boardId, String professorName, Integer year, Semester semester, Pageable pageable);
}
