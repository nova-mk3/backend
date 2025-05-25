package org.nova.backend.member.application.mapper;

import java.util.UUID;
import lombok.AllArgsConstructor;
import org.nova.backend.member.application.dto.response.MemberForListResponse;
import org.nova.backend.member.application.dto.response.MemberResponse;
import org.nova.backend.member.application.dto.response.MemberWithGraduationYearResponse;
import org.nova.backend.member.domain.model.entity.Graduation;
import org.nova.backend.member.domain.model.entity.Member;
import org.nova.backend.member.domain.model.entity.PendingMember;
import org.nova.backend.member.domain.model.entity.ProfilePhoto;
import org.nova.backend.member.domain.model.valueobject.Role;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class MemberMapper {

    private MemberProfilePhotoMapper profilePhotoMapper;

    public Member toEntity(
            PendingMember pendingMember,
            Graduation graduation
    ) {
        return new Member(
                UUID.randomUUID(),
                pendingMember.getStudentNumber(),
                pendingMember.getPassword(),
                pendingMember.getName(),
                pendingMember.getEmail(),
                pendingMember.isGraduation(),
                pendingMember.getGrade(),
                pendingMember.getSemester(),
                pendingMember.isAbsence(),
                false,
                pendingMember.getProfilePhoto(),
                pendingMember.getPhone(),
                pendingMember.getIntroduction(),
                pendingMember.getBirth(),
                Role.GENERAL,
                graduation,
                false
        );
    }

    public MemberResponse toResponse(
            Member member,
            ProfilePhoto profilePhoto
    ) {
        String gradeStr = formatGrade(member.getGrade());
        String semesterStr = member.getSemester() == 0 ? "" : member.getSemester() + "학기";

        return new MemberResponse(
                member.getId(),
                member.getStudentNumber(),
                member.getName(),
                member.getEmail(),
                member.isGraduation(),
                gradeStr,
                semesterStr,
                member.isAbsence(),
                profilePhotoMapper.toResponse(profilePhoto),
                member.getPhone(),
                member.getIntroduction(),
                member.getBirth(),
                member.getRole()
        );
    }

    public MemberForListResponse toResponseForList(Member member, ProfilePhoto profilePhoto) {
        String gradeStr = formatGrade(member.getGrade());
        String semesterStr = member.getSemester() == 0 ? "" : member.getSemester() + "학기";

        return new MemberForListResponse(
                member.getId(),
                member.getStudentNumber(),
                member.getName(),
                member.getEmail(),
                member.isGraduation(),
                gradeStr,
                semesterStr,
                member.isAbsence(),
                profilePhotoMapper.toResponse(profilePhoto),
                member.getPhone()
        );
    }

    public MemberWithGraduationYearResponse toResponseWithGraduationYear(
            Member member,
            ProfilePhoto profilePhoto,
            int graduationYear
    ) {
        String gradeStr = formatGrade(member.getGrade());
        String semesterStr = member.getSemester() == 0 ? "" : member.getSemester() + "학기";

        return new MemberWithGraduationYearResponse(
                member.getId(),
                member.getStudentNumber(),
                member.getName(),
                member.getEmail(),
                member.isGraduation(),
                gradeStr,
                semesterStr,
                member.isAbsence(),
                profilePhotoMapper.toResponse(profilePhoto),
                member.getPhone(),
                member.getIntroduction(),
                member.getBirth(),
                member.getRole(),
                graduationYear == 0 ? null : graduationYear + "년"
        );
    }

    private String formatGrade(int grade) {
        return grade >= 5 ? "초과학기" : grade + "학년";
    }
}