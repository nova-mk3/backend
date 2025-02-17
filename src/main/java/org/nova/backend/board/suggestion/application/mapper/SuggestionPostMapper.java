package org.nova.backend.board.suggestion.application.mapper;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.nova.backend.board.suggestion.application.dto.request.SuggestionPostRequest;
import org.nova.backend.board.suggestion.application.dto.response.SuggestionFileResponse;
import org.nova.backend.board.suggestion.application.dto.response.SuggestionPostDetailResponse;
import org.nova.backend.board.suggestion.application.dto.response.SuggestionPostSummaryResponse;
import org.nova.backend.board.suggestion.domain.model.entity.SuggestionPost;
import org.nova.backend.member.domain.model.entity.Member;
import org.springframework.stereotype.Component;

@Component
public class SuggestionPostMapper {

    public SuggestionPost toEntity(
            SuggestionPostRequest request,
            Member member
    ) {
        return new SuggestionPost(
                UUID.randomUUID(),
                member,
                request.getTitle(),
                request.getContent(),
                request.getIsPrivate(),
                false,
                false,
                null,
                LocalDateTime.now(),
                LocalDateTime.now(),
                new ArrayList<>()
        );
    }

    public SuggestionPostDetailResponse toDetailResponse(SuggestionPost post){
        List<SuggestionFileResponse> fileResponses = post.getFiles().stream()
                .map(file -> new SuggestionFileResponse(
                        file.getId(),
                        file.getOriginalFilename(),
                        "/api/v1/files/" + file.getId() + "/download"
                ))
                .toList();
        return new SuggestionPostDetailResponse(
                post.getId(),
                post.getTitle(),
                post.getContent(),
                post.getCreatedTime(),
                post.getModifiedTime(),
                post.isPrivate(),
                post.getAdminReply(),
                fileResponses,
                post.getMember().getName()
        );
    }

    public SuggestionPostSummaryResponse toSummaryResponse(
            SuggestionPost post,
            UUID currentUserId
    ) {
        boolean isAuthor = post.getMember().getId().equals(currentUserId);
        return new SuggestionPostSummaryResponse(
                post.getId(),
                post.getTitle(),
                post.getCreatedTime(),
                post.getModifiedTime(),
                post.isPrivate(),
                post.isAnswered(),
                post.isAnswerRead(),
                isAuthor
        );
    }
}
