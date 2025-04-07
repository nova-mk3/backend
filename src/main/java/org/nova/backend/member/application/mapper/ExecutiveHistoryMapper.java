package org.nova.backend.member.application.mapper;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.nova.backend.member.application.dto.request.AddExecutiveHistoryRequest;
import org.nova.backend.member.application.dto.response.ExecutiveHistoryResponse;
import org.nova.backend.member.application.dto.response.ProfilePhotoResponse;
import org.nova.backend.member.domain.model.entity.ExecutiveHistory;
import org.nova.backend.member.domain.model.entity.Member;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ExecutiveHistoryMapper {

    private final MemberProfilePhotoMapper memberProfilePhotoMapper;

    public ExecutiveHistory toEntity(AddExecutiveHistoryRequest request, Member member) {
        String name = request.getName();
        if (name == null || name.isBlank()) {
            name = member.getName();
        }
        return new ExecutiveHistory(
                UUID.randomUUID(),
                request.getYear(),
                request.getRole(),
                name,
                member
        );
    }

    public ExecutiveHistoryResponse toResponse(ExecutiveHistory executiveHistory) {
        ProfilePhotoResponse profilePhotoResponse = executiveHistory.getMember() != null ?
                memberProfilePhotoMapper.toResponse(executiveHistory.getMember().getProfilePhoto())
                : memberProfilePhotoMapper.toResponse(null);

        Member executive = executiveHistory.getMember();

        return new ExecutiveHistoryResponse(
                executiveHistory.getId(),
                executiveHistory.getYear(),
                executiveHistory.getRole(),
                executiveHistory.getName(),
                executive != null ? executive.getId() : null,
                executive != null ? executive.getStudentNumber() : null,
                profilePhotoResponse,
                executive != null ? executive.getPhone() : null,
                executive != null ? executive.getGrade() + "학년" : null,
                executive != null ? executive.getSemester() + "학기" : null
        );
    }

}
