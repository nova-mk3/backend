package org.nova.backend.auth.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.nova.backend.auth.application.dto.request.GraduationSignUpRequest;
import org.nova.backend.auth.application.dto.request.MemberSignUpRequest;
import org.nova.backend.auth.application.dto.request.SignUpRequest;
import org.nova.backend.member.adapter.repository.GraduationRepository;
import org.nova.backend.member.adapter.repository.MemberRepository;
import org.nova.backend.member.application.mapper.GraduationMapper;
import org.nova.backend.member.application.mapper.MemberMapper;
import org.nova.backend.member.domain.exception.MemberDomainException;
import org.nova.backend.member.domain.model.entity.Graduation;
import org.nova.backend.member.domain.model.entity.Member;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class SignUpService {

    private final MemberMapper memberMapper;
    private final GraduationMapper graduationMapper;
    private final MemberRepository memberRepository;
    private final GraduationRepository graduationRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    /**
     * 회원가입
     *
     * @param signUpRequest 회원가입 요청
     * @return 저장된 회원 객체
     */
    @Transactional
    public Member signUp(final SignUpRequest signUpRequest) {

        MemberSignUpRequest memberRequest = signUpRequest.getMemberSignUpRequest();

        isAlreadyExist(memberRequest.getStudentNumber());

        Graduation graduation = null;
        if (memberRequest.isGraduation()) {
            graduation = createGraduation(signUpRequest.getGraduationSignUpRequest());
        }

        return createMember(signUpRequest.getMemberSignUpRequest(), graduation);
    }

    private Graduation createGraduation(final GraduationSignUpRequest request) {
        Graduation signupGraduation = graduationMapper.toEntity(request);
        return graduationRepository.save(signupGraduation);
    }

    private Member createMember(final MemberSignUpRequest request, final Graduation graduation) {
        String encryptedPassword = bCryptPasswordEncoder.encode(request.getPassword());
        Member signUpMember = memberMapper.toEntity(request, encryptedPassword, graduation);
        return memberRepository.save(signUpMember);
    }

    private void isAlreadyExist(final String studentNumber) {
        if (memberRepository.existsByStudentNumber(studentNumber)) {
            throw new MemberDomainException("Member already exists " + studentNumber);
        }
    }

}
