package org.nova.backend.email.application.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.nova.backend.email.domain.exception.EmailException;
import org.nova.backend.email.domain.model.EmailAuth;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
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

    @Value("${spring.mail.sender}")
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
            mimeMessageHelper.setText(emailBody.toString(), true);

            javaMailSender.send(mimeMessage);

        } catch (MessagingException e) {
            throw new EmailException("email send failed." + emailAuth.getEmail(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 임시 비밀번호 이메일 전송
     *
     * @param toEmail 수신자 이메일 주소
     * @param tempPassword 임시 비밀번호
     */
    public void sendTempPasswordEmail(String toEmail, String tempPassword) {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper;

        try {
            mimeMessageHelper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            mimeMessageHelper.setFrom(hostAddress);
            mimeMessageHelper.setTo(toEmail);
            mimeMessageHelper.setSubject("[Nova] 임시 비밀번호 발급");

            StringBuilder emailBody = new StringBuilder();
            appendTempPasswordEmailBody(emailBody, tempPassword);
            mimeMessageHelper.setText(emailBody.toString(), true);

            javaMailSender.send(mimeMessage);

        } catch (MessagingException e) {
            throw new EmailException("임시 비밀번호 이메일 전송 실패: " + toEmail, HttpStatus.INTERNAL_SERVER_ERROR);
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
                "<body style='font-family: Arial, sans-serif; background-color: #f5f7fa; margin: 0; padding: 0; text-align: center;'>");
        emailBody.append(
                "<div style='max-width: 600px; margin: 40px auto; background: #fff; padding: 40px; border-radius: 12px; box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1); text-align: center;'>");
        emailBody.append(
                "<img src='https://nova.cbnu.ac.kr/files/public/novaLogo.png' alt='NOVA 로고' style='width: 100px; margin-bottom: 20px;'>");
        emailBody.append(
                "<div style='font-size: 24px; font-weight: bold; color: #7D60EC; margin-bottom: 10px;'>Nova 이메일 인증</div>");
        emailBody.append(
                "<p style='font-size: 16px; color: #666; margin-top: 10px; '>안녕하세요! NOVA 홈페이지 회원가입을 위한<br>인증 코드를 발급해 드립니다.</p>");
        emailBody.append(
                "<div style='background: #E6E0F8; padding: 15px; border-radius: 8px; font-size: 24px; font-weight: bold; color: #7D60EC; display: inline-block; letter-spacing: 3px; margin: 20px auto;'>"
                        + authCode + "</div>");
        emailBody.append("<p style='font-size: 15px; color: #666; margin-bottom: 30px;'>위 코드를 입력하여 이메일 인증을 완료해주세요.</p>");
        emailBody.append("<div style='margin-top: 20px;'>");
        emailBody.append(
                "<a href='https://nova.cbnu.ac.kr/signin' style='display: inline-block; padding: 12px 20px; background: #7D60EC; color: #fff; text-decoration: none; font-size: 16px; font-weight: bold; border-radius: 8px;'>Nova 로그인</a>");
        emailBody.append("</div>");
        emailBody.append(
                "<p style='margin-top: 30px; font-size: 12px; color: #888;'>이 메일은 자동 발송된 메일입니다.<br>문의사항은 <a href='mailto:nova_cbnu@naver.com' style='color: #7D60EC;'>nova_cbnu@naver.com</a>으로 연락주세요.</p>");
        emailBody.append("</div>");
        emailBody.append("</body>");
        emailBody.append("</html>");
    }

    private void appendTempPasswordEmailBody(StringBuilder emailBody, String tempPassword) {
        emailBody.append("<!DOCTYPE html>");
        emailBody.append("<html lang='ko'>");
        emailBody.append("<head>");
        emailBody.append("<meta charset='UTF-8'>");
        emailBody.append("<meta name='viewport' content='width=device-width, initial-scale=1.0'>");
        emailBody.append("<title>NOVA 임시 비밀번호</title>");
        emailBody.append("</head>");
        emailBody.append("<body style='font-family: Arial, sans-serif; background-color: #f5f7fa; margin: 0; padding: 0; text-align: center;'>");
        emailBody.append("<div style='max-width: 600px; margin: 40px auto; background: #fff; padding: 40px; border-radius: 12px; box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1); text-align: center;'>");
        emailBody.append("<img src='https://nova.cbnu.ac.kr/files/public/novaLogo.png' alt='NOVA 로고' style='width: 100px; margin-bottom: 20px;'>");
        emailBody.append("<div style='font-size: 24px; font-weight: bold; color: #7D60EC; margin-bottom: 10px;'>임시 비밀번호 안내</div>");
        emailBody.append("<p style='font-size: 16px; color: #666; margin-top: 10px;'>요청하신 임시 비밀번호를 아래에 발급해드립니다.</p>");
        emailBody.append("<div style='background: #E6E0F8; padding: 15px; border-radius: 8px; font-size: 24px; font-weight: bold; color: #7D60EC; display: inline-block; letter-spacing: 2px; margin: 20px auto;'>"
                + tempPassword + "</div>");
        emailBody.append("<p style='font-size: 15px; color: #666; margin-bottom: 30px;'>로그인 후 반드시 비밀번호를 변경해주세요.</p>");
        emailBody.append("<div style='margin-top: 20px;'>");
        emailBody.append("<a href='https://nova.cbnu.ac.kr/signin' style='display: inline-block; padding: 12px 20px; background: #7D60EC; color: #fff; text-decoration: none; font-size: 16px; font-weight: bold; border-radius: 8px;'>Nova 로그인</a>");
        emailBody.append("</div>");
        emailBody.append("<p style='margin-top: 30px; font-size: 12px; color: #888;'>이 메일은 자동 발송된 메일입니다.<br>문의사항은 <a href='mailto:nova_cbnu@naver.com' style='color: #7D60EC;'>nova_cbnu@naver.com</a>으로 연락주세요.</p>");
        emailBody.append("</div>");
        emailBody.append("</body>");
        emailBody.append("</html>");
    }

}
