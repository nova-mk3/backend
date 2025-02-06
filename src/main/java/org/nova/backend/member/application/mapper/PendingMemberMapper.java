package org.nova.backend.member.application.mapper;

import java.util.UUID;
import org.nova.backend.auth.application.dto.request.MemberSignUpRequest;
import org.nova.backend.member.application.dto.response.PendingMemberResponse;
import org.nova.backend.member.domain.model.entity.PendingGraduation;
import org.nova.backend.member.domain.model.entity.PendingMember;
import org.springframework.stereotype.Component;

@Component
public class PendingMemberMapper {

    public PendingMember toEntity(MemberSignUpRequest request, String encryptedPassword,
                                  PendingGraduation pendingGraduation) {
        return new PendingMember(
                UUID.randomUUID(),
                request.getStudentNumber(),
                encryptedPassword,
                request.getName(),
                request.getEmail(),
                request.isGraduation(),
                request.getYear(),
                request.getSemester(),
                request.isAbsence(),
                request.getProfilePhoto(),
                request.getPhone(),
                "안녕하세요^-^",
                request.getBirth(),
                pendingGraduation,
                false
        );
    }

    public PendingMemberResponse toResponse(PendingMember pendingMember) {
        return new PendingMemberResponse(
                pendingMember.getId(),
                pendingMember.getStudentNumber(),
                pendingMember.getName(),
                pendingMember.getEmail(),
                pendingMember.isGraduation(),
                pendingMember.getYear(),
                pendingMember.getSemester(),
                pendingMember.isAbsence(),
                pendingMember.getProfilePhoto(),
                pendingMember.getPhone(),
                pendingMember.getIntroduction(),
                pendingMember.getBirth(),
                pendingMember.isRejected()
        );
    }

}
