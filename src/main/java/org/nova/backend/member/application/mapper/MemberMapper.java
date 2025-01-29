package org.nova.backend.member.application.mapper;

import java.util.UUID;
import org.nova.backend.auth.application.dto.request.SignUpRequest;
import org.nova.backend.auth.application.dto.response.MemberResponse;
import org.nova.backend.member.domain.model.entity.Member;
import org.springframework.stereotype.Component;

@Component
public class MemberMapper {

    public Member toEntity(SignUpRequest request, String encryptedPassword) {
        return new Member(
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
                request.getRole(),
                null
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
