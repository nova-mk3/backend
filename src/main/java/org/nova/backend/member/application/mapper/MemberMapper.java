package org.nova.backend.member.application.mapper;

import java.util.UUID;
import lombok.AllArgsConstructor;
import org.nova.backend.member.application.dto.response.MemberForListResponse;
import org.nova.backend.member.application.dto.response.MemberResponse;
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

    public Member toEntity(PendingMember pendingMember, Graduation graduation) {
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
                pendingMember.getProfilePhoto(),
                pendingMember.getPhone(),
                pendingMember.getIntroduction(),
                pendingMember.getBirth(),
                Role.GENERAL,
                graduation,
                false
        );
    }

    public MemberResponse toResponse(Member member, ProfilePhoto profilePhoto) {
        return new MemberResponse(
                member.getId(),
                member.getStudentNumber(),
                member.getName(),
                member.getEmail(),
                member.isGraduation(),
                member.getGrade() <= 4 ? member.getGrade() + "학년" : "초과 학기",
                member.getSemester() + "학기",
                member.isAbsence(),
                profilePhotoMapper.toResponse(profilePhoto),
                member.getPhone(),
                member.getIntroduction(),
                member.getBirth(),
                member.getRole()
        );
    }

    public MemberForListResponse toResponseForList(Member member, ProfilePhoto profilePhoto) {
        return new MemberForListResponse(
                member.getStudentNumber(),
                member.getName(),
                member.getEmail(),
                member.isGraduation(),
                member.getGrade() <= 4 ? member.getGrade() + "학년" : "초과 학기",
                member.isAbsence(),
                profilePhotoMapper.toResponse(profilePhoto),
                member.getPhone()
        );
    }

}
