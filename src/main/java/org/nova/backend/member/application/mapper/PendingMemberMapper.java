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

    private MemberProfilePhotoMapper profilePhotoMapper;

    public PendingMember toEntity(MemberSignUpRequest request, String encryptedPassword, ProfilePhoto profilePhoto,
                                  PendingGraduation pendingGraduation) {
        return new PendingMember(
                UUID.randomUUID(),
                request.getStudentNumber(),
                encryptedPassword,
                request.getName(),
                request.getEmail(),
                request.isGraduation(),
                request.getGrade(),
                request.getSemester(),
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

        return new PendingMemberResponse(
                pendingMember.getId(),
                pendingMember.getStudentNumber(),
                pendingMember.getName(),
                pendingMember.getEmail(),
                pendingMember.isGraduation(),
                pendingMember.getGrade() <= 4 ? pendingMember.getGrade() + "학년" : "초과 학기",
                pendingMember.getSemester() <= 8 ? pendingMember.getSemester() + "학기" : "초과 학기",
                pendingMember.isAbsence(),
                profilePhotoMapper.toResponse(profilePhoto),
                pendingMember.getPhone(),
                pendingMember.getIntroduction(),
                pendingMember.getBirth(),
                pendingMember.isRejected()
        );
    }

}
