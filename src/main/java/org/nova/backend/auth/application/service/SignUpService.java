package org.nova.backend.auth.application.service;

import lombok.RequiredArgsConstructor;
import org.nova.backend.auth.application.dto.request.SignUpRequest;
import org.nova.backend.member.adapter.repository.MemberRepository;
import org.nova.backend.member.application.mapper.MemberMapper;
import org.nova.backend.member.domain.exception.MemberDomainException;
import org.nova.backend.member.domain.model.entity.Member;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class SignUpService {

    private final MemberMapper memberMapper;
    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    /**
     * 회원가입
     *
     * @param signUpRequest 회원가입 요청
     * @return 저장된 회원 객체
     */
    @Transactional
    public Member createMember(final SignUpRequest signUpRequest) {

        isAlreadyExist(signUpRequest.getStudentNumber());

        String encryptedPassword = bCryptPasswordEncoder.encode(signUpRequest.getPassword());
        Member signUpMember = memberMapper.toEntity(signUpRequest, encryptedPassword);
        memberRepository.save(signUpMember);

        return getSavedMember(signUpRequest.getStudentNumber());
    }

    private void isAlreadyExist(String studentNumber) {
        if (memberRepository.existsByStudentNumber(studentNumber)) {
            throw new MemberDomainException("Member already exists " + studentNumber);
        }
    }

    private Member getSavedMember(String studentNumber) {
        return memberRepository.findByStudentNumber(studentNumber)
                .orElseThrow(() -> new MemberDomainException("Member not found " + studentNumber));
    }

}
