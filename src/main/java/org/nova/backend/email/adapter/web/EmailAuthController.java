package org.nova.backend.email.adapter.web;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.nova.backend.email.application.dto.request.AuthCodeEmailRequest;
import org.nova.backend.email.application.dto.request.CheckAuthCodelRequest;
import org.nova.backend.email.application.service.EmailAuthService;
import org.nova.backend.shared.model.ApiResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Email API", description = "이메일 인증 코드 전송 관련 API 목록")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/email-auth")
public class EmailAuthController {

    private final EmailAuthService emailAuthService;

    /**
     * 회원가입 시 이메일 인증코드 전송
     */
    @PostMapping("")
    @EmailAuthApiDocument.EmailAuthApiDoc
    public ApiResponse<String> sendAuthCodeEmail(@RequestBody AuthCodeEmailRequest request) {
        emailAuthService.sendAuthCodeEmail(request.getEmail());

        return ApiResponse.success("이메일이 전송되었습니다. 전송된 인증 코드를 확인해 주세요.");
    }

    /**
     * 회원가입 시 이메일 인증코드 확인
     */
    @PostMapping("/check")
    @EmailAuthApiDocument.CheckAuthCodeApiDoc
    public ApiResponse<String> checkAuthCode(@RequestBody CheckAuthCodelRequest request) {
        emailAuthService.checkAuthCode(request.getEmail(), request.getAuthCode());

        return ApiResponse.success("인증 코드 확인 성공");
    }
}
