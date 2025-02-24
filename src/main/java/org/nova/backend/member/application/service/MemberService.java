package org.nova.backend.member.application.service;

import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.nova.backend.member.adapter.repository.MemberRepository;
import org.nova.backend.member.application.dto.response.MemberResponse;
import org.nova.backend.member.application.mapper.MemberMapper;
import org.nova.backend.member.domain.exception.MemberDomainException;
import org.nova.backend.member.domain.model.entity.Member;
import org.nova.backend.member.domain.model.entity.ProfilePhoto;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    private final ProfilePhotoFileService profilePhotoFileService;

    private final MemberMapper memberMapper;

    /**
     * 모든 회원 리스트 불러오기
     *
     * @return List<MemberResponse>
     */
    public List<MemberResponse> getAllMembers() {
        List<Member> memberList = memberRepository.findAllMembers();

        return memberList.stream().map(this::getMemberResponseFromMember).toList();
    }

    /**
     * id로 회원 찾기
     */
    public Member findByMemberId(UUID memberId) {
        return memberRepository.findMemberById(memberId)
                .orElseThrow(() -> new MemberDomainException("member not found.", HttpStatus.NOT_FOUND));
    }

    /**
     * Member 응답 객체 생성
     *
     * @param member
     * @return MemberResponse
     */
    public MemberResponse getMemberResponseFromMember(Member member) {
        ProfilePhoto profilePhoto = profilePhotoFileService.getProfilePhoto(member.getProfilePhoto());
        return memberMapper.toResponse(member, profilePhoto);
    }

    /**
     * 회원 탈퇴
     *
     * @param studentNumber 학번
     */
    @Transactional
    public void deleteMember(String studentNumber) {
        Member member = getCurrentMember(studentNumber);

        member.setDeleted();
    }

    /**
     * 현재 로그인한 사용자 가져오기
     */
    private Member getCurrentMember(String studentNumber) {
        return memberRepository.findByStudentNumber(studentNumber)
                .orElseThrow(() -> new MemberDomainException("사용자를 찾을 수 없습니다.", HttpStatus.NOT_FOUND));
    }

}
