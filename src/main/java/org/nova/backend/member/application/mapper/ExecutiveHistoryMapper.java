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
        Member executive = executiveHistory.getMember();
        ProfilePhotoResponse profilePhotoResponse = memberProfilePhotoMapper.toResponse(
                executive != null ? executive.getProfilePhoto() : null
        );

        String gradeText = getGradeText(executive);
        String semesterText = getSemesterText(executive);
        Integer graduationYear = getGraduationYear(executive);

        return new ExecutiveHistoryResponse(
                executiveHistory.getId(),
                executiveHistory.getYear(),
                executiveHistory.getRole(),
                executiveHistory.getName(),
                executive != null ? executive.getId() : null,
                executive != null ? executive.getStudentNumber() : null,
                profilePhotoResponse,
                executive != null ? executive.getPhone() : null,
                gradeText,
                semesterText,
                executive != null && executive.isGraduation(),
                graduationYear
        );
    }

    private String getGradeText(Member member) {
        if (member == null) return null;
        if (member.isGraduation()) return "졸업생";
        if (member.getGrade() >= 5) return "초과 학기";
        return member.getGrade() + "학년";
    }

    private String getSemesterText(Member member) {
        if (member == null || member.isGraduation() || member.getGrade() >= 5) return null;
        int s = member.getSemester();
        return (s >= 1 && s <= 2) ? s + "학기" : "1학기";
    }

    private Integer getGraduationYear(Member member) {
        if (member == null || !member.isGraduation() || member.getGraduation() == null) return null;
        return member.getGraduation().getYear();
    }
}
