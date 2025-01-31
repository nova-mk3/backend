package org.nova.backend.email.adapter.web;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.nova.backend.email.application.dto.request.AuthCodeEmailRequest;
import org.nova.backend.email.application.service.EmailService;
import org.nova.backend.shared.model.ApiResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Email API", description = "이메일 전송 관련 API 목록")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class EmailController {

    private final EmailService emailService;

    /**
     * 회원가입 시 이메일 인증코드 전송
     */
    @PostMapping("")
    public ApiResponse<String> sendAuthCodeEmail(@RequestBody AuthCodeEmailRequest request) {
        emailService.sendAuthCodeEmail(request.getEmail());

        return ApiResponse.success("이메일이 전송되었습니다. 전송된 인증 코드를 확인해 주세요.");
    }
}
