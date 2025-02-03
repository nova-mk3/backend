package org.nova.backend.email.application.service;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.nova.backend.email.adapter.persistence.repository.EmailAuthRepository;
import org.nova.backend.email.application.mapper.EmailAuthMapper;
import org.nova.backend.email.domain.exception.EmailAuthException;
import org.nova.backend.email.domain.model.EmailAuth;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class EmailAuthService {

    private final EmailAuthMapper emailAuthMapper;
    private final EmailAuthRepository emailAuthRepository;
    private final EmailSendService emailSendService;

    /**
     * 이메일로 인증 코드 전송
     *
     * @param email 수신 이메일
     */
    @Transactional
    public void sendAuthCodeEmail(String email) {
        EmailAuth emailAuth = createEmailAuth(email);
        emailSendService.sendAuthCodeEmail(emailAuth);
    }

    private EmailAuth createEmailAuth(String email) {
        String authCode = createAuthCode();
        EmailAuth emailAuth = emailAuthMapper.toEntity(email, authCode);
        return emailAuthRepository.save(emailAuth);
    }

    private String createAuthCode() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 10);
    }

    /**
     * 인증 코드 확인
     *
     * @param email
     */
    @Transactional
    public void checkAuthCode(String email, String authCode) {
        EmailAuth emailAuth = emailAuthRepository.findByEmailAndCode(email, authCode)
                .orElseThrow(() -> new EmailAuthException("email verification failed", HttpStatus.UNAUTHORIZED));

        emailAuthRepository.delete(emailAuth);
    }

}
