package org.nova.backend.email.application.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.nova.backend.email.domain.exception.EmailException;
import org.nova.backend.email.domain.model.EmailAuth;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class EmailSendService {

    private final JavaMailSender javaMailSender;

    @Value("${EMAIL_ADDRESS}")
    private String hostAddress;


    /**
     * 인증 코드 이메일 전송
     *
     * @param emailAuth 수신 이메일 주소, 인증 코드
     */
    public void sendAuthCodeEmail(EmailAuth emailAuth) {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper;

        try {
            mimeMessageHelper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            mimeMessageHelper.setFrom(hostAddress);
            mimeMessageHelper.setTo(emailAuth.getEmail());
            mimeMessageHelper.setSubject("[Nova] 이메일 인증 번호 발급");

            StringBuilder emailBody = new StringBuilder();
            appendAuthCodeEmailBody(emailBody, emailAuth.getCode());
            mimeMessageHelper.setText(emailBody.toString(), true);          //html형식으로 설정

            javaMailSender.send(mimeMessage);

        } catch (MessagingException e) {
            throw new EmailException("email send failed.");
        }
    }

    private void appendAuthCodeEmailBody(StringBuilder emailBody, String authCode) {
        emailBody.append("<!DOCTYPE html>");
        emailBody.append("<html lang='ko'>");
        emailBody.append("<head>");
        emailBody.append("<meta charset='UTF-8'>");
        emailBody.append("<meta name='viewport' content='width=device-width, initial-scale=1.0'>");
        emailBody.append("<title>NOVA 이메일 인증</title>");
        emailBody.append("</head>");
        emailBody.append(
                "<body style='font-family: Arial, sans-serif; background-color: #E6E0F8; margin: 0; padding: 0; text-align: center;'>");
        emailBody.append(
                "<div style='max-width: 600px; margin: 40px auto; background: #fff; padding: 40px; border-radius: 12px; box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1); text-align: center;'>");
        emailBody.append(
                "<img src='https://your-logo-url.com/logo.png' alt='NOVA 로고' style='width: 120px; margin-bottom: 20px;'>");
        emailBody.append(
                "<div style='font-size: 24px; font-weight: bold; color: #7D60EC; margin-bottom: 10px;'>Nova 이메일 인증</div>");
        emailBody.append(
                "<p style='font-size: 16px; color: #666; margin-bottom: 20px;'>회원가입을 계속하려면 아래 코드를 입력해주세요.</p>");
        emailBody.append(
                "<div style='background: #E6E0F8; padding: 15px; border-radius: 8px; font-size: 24px; font-weight: bold; color: #7D60EC; display: inline-block; letter-spacing: 3px; margin: 20px auto;'>"
                        + authCode + "</div>");
        emailBody.append("<div style='margin-top: 20px;'>");
        emailBody.append(
                "<a href='www.nova.cbnu.ac.kr' style='display: inline-block; padding: 12px 20px; background: #7D60EC; color: #fff; text-decoration: none; font-size: 16px; font-weight: bold; border-radius: 8px;'>Nova 로그인</a>");
        emailBody.append("</div>");
        emailBody.append(
                "<p style='margin-top: 30px; font-size: 12px; color: #888;'>이 메일은 자동 발송된 메일입니다.<br>문의사항은 <a href='mailto:nova_cbnu@naver.com' style='color: #7D60EC;'>nova_cbnu@naver.com</a>으로 연락주세요.</p>");
        emailBody.append("</div>");
        emailBody.append("</body>");
        emailBody.append("</html>");
    }
}
