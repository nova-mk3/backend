package org.nova.backend.member.application.service;

import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.nova.backend.member.adapter.repository.GraduationRepository;
import org.nova.backend.member.adapter.repository.MemberRepository;
import org.nova.backend.member.application.dto.request.UpdateMemberRequest;
import org.nova.backend.member.application.dto.response.AdminMemberResponse;
import org.nova.backend.member.domain.exception.MemberDomainException;
import org.nova.backend.member.domain.model.entity.Graduation;
import org.nova.backend.member.domain.model.entity.Member;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AdminService {

    @Value("${admin.student.number}")
    private String adminStudentNumber;

    private final MemberService memberService;
    private final MemberRepository memberRepository;
    private final GraduationRepository graduationRepository;

    /**
     * @param member 관리되는 member 객체
     * @return AdminMemberResponse 관리자를 위한 member response
     */
    public AdminMemberResponse getAdminMemberResponse(Member member) {
        return new AdminMemberResponse(memberService.getMemberResponseFromMember(member),
                memberService.getGraduationFromMember(member.getGraduation()));
    }

    /**
     * 전체 학기 일괄 증가 : 재학생들의 학기를 일괄 증가합니다. 졸업생, 휴학생 제외
     */
    @Transactional
    public void updateAllSemesters() {

        List<Member> inSchoolMembers = memberRepository.findMembersInSchool(adminStudentNumber);
        if (inSchoolMembers.isEmpty()) {
            throw new MemberDomainException("업데이트 할 멤버가 없습니다.", HttpStatus.NOT_FOUND);
        }

        inSchoolMembers.forEach(Member::updateSemester);

        inSchoolMembers.forEach(member -> {
            if (member.getSemester() % 2 == 1) { // 홀수 학기일 때만 학년 업데이트
                member.updateGrade();
            }
        });
    }

    /**
     * 졸업생 정보 변경
     *
     * @param memberId
     */
    @Transactional
    public AdminMemberResponse updateGraduation(UUID memberId, boolean isGraduation) {
        Member member = memberService.findByMemberId(memberId);

        member.updateGraduation(isGraduation);
        if (!isGraduation) {  // 졸업 x로 바꾸는 경우
            return getAdminMemberResponse(member);
        }
        // 졸업 o로 바꾸는 경우
        member.updateAbsence(false);  // 졸업생은 휴학 중이 아닙니다.

        if (member.getGraduation() == null) { // 졸업 정보가 없는 경우 추가
            Graduation graduation = new Graduation(UUID.randomUUID(), 0, false, false, "", "", "");  // 임시 정보
            graduation = graduationRepository.save(graduation);
            member.updateGraduationInfo(graduation);
        }
        return getAdminMemberResponse(member);
    }

    /**
     * 관리자 회원 정보 수정
     */
    @Transactional
    public AdminMemberResponse updateMemberProfile(UUID memberId, UpdateMemberRequest updateMemberRequest) {
        Member member = memberService.findByMemberId(memberId);

        //기본 회원 정보 수정
        memberService.updateMemberProfile(member, updateMemberRequest.getUpdateMemberProfileRequest());
        //졸업생 정보 수정
        memberService.handleGraduation(member, updateMemberRequest);

        return getAdminMemberResponse(member);
    }

    /**
     * 관리자 권한으로 회원 탈퇴
     *
     * @param memberId 탈퇴시킬 회원 id
     */
    @Transactional
    public void deleteMember(UUID memberId) {
        Member member = memberService.findByMemberId(memberId);
        member.setDeleted();
    }

}
