package org.nova.backend.auth.application.service;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.nova.backend.auth.application.dto.request.PasswordResetRequest;
import org.nova.backend.email.application.service.EmailSendService;
import org.nova.backend.member.adapter.repository.MemberRepository;
import org.nova.backend.member.domain.exception.MemberDomainException;
import org.nova.backend.member.domain.model.entity.Member;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PasswordResetService {
    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final EmailSendService emailSendService;

    public void resetPassword(PasswordResetRequest request) {
        log.info("비밀번호 초기화 시작");
        try {
            Member member = memberRepository.findByNameAndEmail(request.getName(), request.getEmail())
                    .orElseThrow(() -> new MemberDomainException("사용자를 찾을 수 없습니다.", HttpStatus.NOT_FOUND));
            log.info("회원 조회 성공 - 사용자 ID: {}", member.getId());

            String tempPassword = generateTempPassword();
            member.resetPasswordWithTemp(passwordEncoder.encode(tempPassword));
            memberRepository.save(member);
            log.info("임시 비밀번호 저장 완료 - 사용자 ID: {}", member.getId());

            emailSendService.sendTempPasswordEmail(member.getId(), member.getEmail(), tempPassword);
            log.info("임시 비밀번호 메일 발송 완료 - 사용자 ID: {}", member.getId());
        } catch (Exception e) {
            log.error("비밀번호 초기화 중 에러 발생", e);
            throw e;
        }
    }

    private String generateTempPassword() {
        return UUID.randomUUID().toString().substring(0, 10);
    }
}
