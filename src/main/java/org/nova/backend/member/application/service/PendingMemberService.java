package org.nova.backend.member.application.service;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.nova.backend.member.adapter.repository.GraduationRepository;
import org.nova.backend.member.adapter.repository.MemberRepository;
import org.nova.backend.member.adapter.repository.PendingMemberRepository;
import org.nova.backend.member.application.mapper.GraduationMapper;
import org.nova.backend.member.application.mapper.MemberMapper;
import org.nova.backend.member.domain.exception.PendingMemberDomainException;
import org.nova.backend.member.domain.model.entity.Graduation;
import org.nova.backend.member.domain.model.entity.Member;
import org.nova.backend.member.domain.model.entity.PendingGraduation;
import org.nova.backend.member.domain.model.entity.PendingMember;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PendingMemberService {

    private final PendingMemberRepository pendingMemberRepository;

    private final MemberRepository memberRepository;
    private final GraduationRepository graduationRepository;

    private final MemberMapper memberMapper;
    private final GraduationMapper graduationMapper;

    /**
     * 회원가입 요청 단건 승인
     *
     * @param pendingMemberId 회원가입 요청 대상
     * @return Member 생성된 Member 객체
     */
    @Transactional
    public Member approveMemberService(final UUID pendingMemberId) {
        PendingMember pendingMember = findPendingMember(pendingMemberId);

        Graduation graduation = null;
        if (pendingMember.isGraduation()) {
            PendingGraduation pendingGraduation = pendingMember.getPendingGraduation();
            graduation = saveGraduation(pendingGraduation);
        }

        Member member = memberMapper.toEntity(pendingMember, graduation);
        Member savedMember = memberRepository.save(member);

        pendingMemberRepository.delete(pendingMember);
        return savedMember;
    }


    private Graduation saveGraduation(final PendingGraduation pendingGraduation) {
        Graduation graduation = graduationMapper.toEntity(pendingGraduation);
        return graduationRepository.save(graduation);
    }

    private PendingMember findPendingMember(final UUID pendingMemberId) {
        return pendingMemberRepository.findById(pendingMemberId)
                .orElseThrow(() -> new PendingMemberDomainException("pending member not found " + pendingMemberId,
                        HttpStatus.NOT_FOUND));
    }
}
