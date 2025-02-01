package org.nova.backend.auth.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.nova.backend.auth.application.dto.request.GraduationSignUpRequest;
import org.nova.backend.auth.application.dto.request.MemberSignUpRequest;
import org.nova.backend.auth.application.dto.request.SignUpRequest;
import org.nova.backend.member.adapter.repository.MemberRepository;
import org.nova.backend.member.adapter.repository.PendingGraduationRepository;
import org.nova.backend.member.adapter.repository.PendingMemberRepository;
import org.nova.backend.member.application.mapper.PendingGraduationMapper;
import org.nova.backend.member.application.mapper.PendingMemberMapper;
import org.nova.backend.member.domain.exception.MemberDomainException;
import org.nova.backend.member.domain.exception.PendingMemberDomainException;
import org.nova.backend.member.domain.model.entity.PendingGraduation;
import org.nova.backend.member.domain.model.entity.PendingMember;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class SignUpService {

    private final MemberRepository memberRepository;

    private final PendingMemberMapper pendingMemberMapper;
    private final PendingGraduationMapper pendingGraduationMapper;
    private final PendingMemberRepository pendingMemberRepository;
    private final PendingGraduationRepository pendingGraduationRepository;

    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    /**
     * 회원가입
     *
     * @param signUpRequest 회원가입 요청
     * @return 저장된 PendingMember 객체
     */
    @Transactional
    public PendingMember signUp(final SignUpRequest signUpRequest) {

        MemberSignUpRequest memberRequest = signUpRequest.getMemberSignUpRequest();

        isMemberAlreadyExist(memberRequest.getStudentNumber(), memberRequest.getEmail());
        isPendingMemberAlreadyExist(memberRequest.getStudentNumber(), memberRequest.getEmail());

        PendingGraduation pendingGraduation = null;
        if (memberRequest.isGraduation()) {
            pendingGraduation = createPendingGraduation(signUpRequest.getGraduationSignUpRequest());
        }

        return createPendingMember(signUpRequest.getMemberSignUpRequest(), pendingGraduation);
    }

    private PendingGraduation createPendingGraduation(final GraduationSignUpRequest request) {
        PendingGraduation signupGraduation = pendingGraduationMapper.toEntity(request);
        return pendingGraduationRepository.save(signupGraduation);
    }

    private PendingMember createPendingMember(final MemberSignUpRequest request,
                                              final PendingGraduation pendingGraduation) {
        String encryptedPassword = bCryptPasswordEncoder.encode(request.getPassword());
        PendingMember signUpMember = pendingMemberMapper.toEntity(request, encryptedPassword, pendingGraduation);
        return pendingMemberRepository.save(signUpMember);
    }

    private void isMemberAlreadyExist(final String studentNumber, final String email) {
        if (memberRepository.existsByStudentNumberOrEmail(studentNumber, email)) {
            throw new MemberDomainException(
                    "Member already exists. check student number or email " + studentNumber + " " + email);
        }
    }

    private void isPendingMemberAlreadyExist(final String studentNumber, final String email) {
        if (pendingMemberRepository.existsByStudentNumberOrEmail(studentNumber, email)) {
            throw new PendingMemberDomainException(
                    "Pending Member already exists. check student number or email " + studentNumber + " " + email);
        }
    }

}
