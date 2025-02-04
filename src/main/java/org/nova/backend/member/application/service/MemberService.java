package org.nova.backend.member.application.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.nova.backend.member.adapter.repository.MemberRepository;
import org.nova.backend.member.application.dto.response.MemberResponse;
import org.nova.backend.member.application.mapper.MemberMapper;
import org.nova.backend.member.domain.model.entity.Member;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    private final MemberMapper memberMapper;

    /**
     * 모든 회원 리스트 불러오기
     *
     * @return List<MemberResponse>
     */
    public List<MemberResponse> getAllMembers() {
        List<Member> memberList = memberRepository.findAll();
        return memberList.stream().map(memberMapper::toResponse).toList();
    }
}
