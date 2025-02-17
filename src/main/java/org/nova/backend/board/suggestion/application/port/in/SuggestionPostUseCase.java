package org.nova.backend.board.suggestion.application.port.in;

import java.util.UUID;
import org.nova.backend.board.suggestion.application.dto.request.SuggestionPostRequest;
import org.nova.backend.board.suggestion.application.dto.request.SuggestionReplyRequest;
import org.nova.backend.board.suggestion.application.dto.response.SuggestionPostDetailResponse;
import org.nova.backend.board.suggestion.application.dto.response.SuggestionPostSummaryResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SuggestionPostUseCase {
    SuggestionPostDetailResponse createPost(SuggestionPostRequest request, UUID memberId);
    Page<SuggestionPostSummaryResponse> getAllPosts(Pageable pageable, UUID currentUserId);
    SuggestionPostDetailResponse getPostById(UUID postId, UUID memberId);
    void addAdminReply(UUID postId, SuggestionReplyRequest request, UUID adminId);
    void markAnswerAsRead(UUID postId, UUID memberId);
}
