package org.nova.backend.auth.adapter.web;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.nova.backend.auth.application.dto.request.PasswordResetRequest;
import org.nova.backend.auth.application.service.PasswordResetService;
import org.nova.backend.shared.model.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Login API", description = "로그인 관련 API 목록")
@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/members")
public class PasswordResetController {

    private final PasswordResetService passwordResetService;

    @PostMapping("/reset-password")
    @AuthApiDocument.PasswordResetApiDoc
    public ResponseEntity<ApiResponse<String>> resetPassword(@RequestBody PasswordResetRequest request) {
        log.info("비밀번호 초기화 요청 수신");
        passwordResetService.resetPassword(request);
        log.info("비밀번호 초기화 요청 처리 완료");
        return ResponseEntity.ok(ApiResponse.success("임시 비밀번호가 이메일로 전송되었습니다."));
    }
}
