package org.nova.backend.auth.adapter.web;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.nova.backend.auth.application.dto.request.LoginRequest;
import org.nova.backend.shared.model.ApiResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Login API", description = "로그인 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/members")
public class LoginController {

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
}
