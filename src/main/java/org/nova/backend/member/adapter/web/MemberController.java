package org.nova.backend.member.adapter.web;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.nova.backend.auth.adapter.web.AuthApiDocument;
import org.nova.backend.member.application.service.MemberService;
import org.nova.backend.shared.model.ApiResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Member API", description = "회원 정보를 관리합니다.")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/members")
public class MemberController {

    private final MemberService memberService;

    @DeleteMapping("")
    @AuthApiDocument.WithdrawalApiDoc
    public ApiResponse<String> withdrawal(HttpServletResponse response) {
        memberService.deleteMember(getCurrentMember());

        // auth token 담은 쿠키 제거
        Cookie cookie = new Cookie("AUTH_TOKEN", null);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);

        return ApiResponse.success("회원 탈퇴 성공");
    }

    /**
     * 현재 로그인한 사용자 학번 가져오기
     */
    private String getCurrentMember() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }
}
