package org.nova.backend.auth.adapter.web;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.nova.backend.auth.application.dto.request.LoginRequest;
import org.nova.backend.member.domain.exception.MemberDomainException;
import org.nova.backend.shared.jwt.JWTUtil;
import org.nova.backend.shared.model.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Login API", description = "로그인 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/members")
public class LoginController {

    private final JWTUtil jwtUtil;

    @PostMapping("/login")
    @AuthApiDocument.LoginApiDoc
    public ApiResponse<String> login(@RequestBody LoginRequest loginRequest) {

        return ApiResponse.success("로그인 성공");
    }

    @PostMapping("/logout")
    @AuthApiDocument.LogoutApiDoc
    public ApiResponse<String> logout() {

        return ApiResponse.success("로그이웃 성공");
    }

    @PostMapping("/access-token/verify")
    @AuthApiDocument.VerifyAccessTokenApiDoc
    public ResponseEntity<ApiResponse<String>> verifyAccessToken(String accessToken) {
        if (accessToken != null && accessToken.startsWith("Bearer ")) {
            accessToken = accessToken.substring(7); // "Bearer " 제거
        }

        if (jwtUtil.isExpired(accessToken)) {
            throw new MemberDomainException("Access token 만료", HttpStatus.UNAUTHORIZED);
        }

        return ResponseEntity.ok().body(ApiResponse.success("Access token 만료 여부 검증 성공"));
    }
}
