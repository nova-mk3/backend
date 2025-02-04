package org.nova.backend.member.application.mapper;

import java.util.UUID;
import org.nova.backend.member.application.dto.request.AddExecutiveHistoryRequest;
import org.nova.backend.member.application.dto.response.ExecutiveHistoryResponse;
import org.nova.backend.member.domain.model.entity.ExecutiveHistory;
import org.nova.backend.member.domain.model.entity.Member;
import org.springframework.stereotype.Component;

@Component
public class ExecutiveHistoryMapper {

    public ExecutiveHistory toEntity(AddExecutiveHistoryRequest request, Member member) {
        return new ExecutiveHistory(
                UUID.randomUUID(),
                request.getYear(),
                request.getRole(),
                request.getName(),
                member
        );
    }

    public ExecutiveHistoryResponse toResponse(ExecutiveHistory executiveHistory) {
        return new ExecutiveHistoryResponse(
                executiveHistory.getId(),
                executiveHistory.getYear(),
                executiveHistory.getRole(),
                executiveHistory.getName(),
                executiveHistory.getMember().getId()
        );
    }

}
