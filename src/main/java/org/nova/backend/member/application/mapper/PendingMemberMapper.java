package org.nova.backend.member.application.mapper;

import java.util.UUID;
import lombok.AllArgsConstructor;
import org.nova.backend.auth.application.dto.request.MemberSignUpRequest;
import org.nova.backend.member.application.dto.response.PendingMemberResponse;
import org.nova.backend.member.domain.model.entity.PendingGraduation;
import org.nova.backend.member.domain.model.entity.PendingMember;
import org.nova.backend.member.domain.model.entity.ProfilePhoto;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class PendingMemberMapper {

    private GradeSemesterYearMapper gradeSemesterYearMapper;
    private MemberProfilePhotoMapper profilePhotoMapper;

    public PendingMember toEntity(MemberSignUpRequest request, String encryptedPassword, ProfilePhoto profilePhoto,
                                  PendingGraduation pendingGraduation) {

        int grade = gradeSemesterYearMapper.toIntGrade(request.getGrade());  //학년
        int semester = gradeSemesterYearMapper.toIntCompletionSemester(request.getSemester()); //학기

        return new PendingMember(
                UUID.randomUUID(),
                request.getStudentNumber(),
                encryptedPassword,
                request.getName(),
                request.getEmail(),
                request.isGraduation(),
                grade,
                semester,
                request.isAbsence(),
                profilePhoto,
                request.getPhone(),
                "안녕하세요^-^",
                request.getBirth(),
                pendingGraduation,
                false
        );
    }

    public PendingMemberResponse toResponse(PendingMember pendingMember, ProfilePhoto profilePhoto) {
        String gradeStr = formatGrade(pendingMember.getGrade());
        String semesterStr = pendingMember.getSemester() == 0 ? "" : pendingMember.getSemester() + "학기";

        return new PendingMemberResponse(
                pendingMember.getId(),
                pendingMember.getStudentNumber(),
                pendingMember.getName(),
                pendingMember.getEmail(),
                pendingMember.isGraduation(),
                gradeStr,
                semesterStr,
                pendingMember.isAbsence(),
                profilePhotoMapper.toResponse(profilePhoto),
                pendingMember.getPhone(),
                pendingMember.getIntroduction(),
                pendingMember.getBirth(),
                pendingMember.isRejected()
        );
    }

    private String formatGrade(int grade) {
        return grade >= 5 ? "초과학기" : grade + "학년";
    }
}