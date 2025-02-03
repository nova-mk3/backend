package org.nova.backend.member.application.mapper;

import java.util.UUID;
import org.nova.backend.member.application.dto.response.MemberResponse;
import org.nova.backend.member.domain.model.entity.Graduation;
import org.nova.backend.member.domain.model.entity.Member;
import org.nova.backend.member.domain.model.entity.PendingMember;
import org.springframework.stereotype.Component;

@Component
public class MemberMapper {

    public Member toEntity(PendingMember pendingMember, Graduation graduation) {
        return new Member(
                UUID.randomUUID(),
                pendingMember.getStudentNumber(),
                pendingMember.getPassword(),
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
                pendingMember.getRole(),
                graduation,
                false
        );
    }

    public MemberResponse toResponse(Member member) {
        return new MemberResponse(
                member.getId(),
                member.getStudentNumber(),
                member.getName(),
                member.getEmail(),
                member.isGraduation(),
                member.getYear(),
                member.getSemester(),
                member.isAbsence(),
                member.getProfilePhoto(),
                member.getPhone(),
                member.getIntroduction(),
                member.getBirth(),
                member.getRole()
        );
    }

}
