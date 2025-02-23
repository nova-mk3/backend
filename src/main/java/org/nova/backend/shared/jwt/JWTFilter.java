package org.nova.backend.shared.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.nova.backend.auth.application.dto.dto.CustomUserDetails;
import org.nova.backend.member.domain.model.entity.Member;
import org.nova.backend.member.domain.model.valueobject.Role;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

@RequiredArgsConstructor
@Slf4j
public class JWTFilter extends OncePerRequestFilter {

    private final JWTUtil jwtUtil;

    /*
    로그인 인증 토큰 기반 로그인 인증 세션(SecurityContextHolder) 생성
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // CORS 헤더 추가
        response.setHeader("Access-Control-Allow-Origin",
                "http://localhost:8080, http://localhost:3000, http://localhost:3001");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Authorization, Content-Type");
        response.setHeader("Access-Control-Expose-Headers", "Authorization");
        response.setHeader("Access-Control-Allow-Credentials", "true");

        String token = getTokenFromCookie(request);

        if (token == null) {
            log.info("token null");
            filterChain.doFilter(request, response);
            return;
        }

        if (jwtUtil.isExpired(token)) {  //토큰 소멸 시간 검증
            log.info("token expired");
            filterChain.doFilter(request, response);
            return;
        }

        UsernamePasswordAuthenticationToken authToken = createAuthToken(token);
        SecurityContextHolder.getContext().setAuthentication(authToken);

        filterChain.doFilter(request, response);
    }

    // 쿠키에서 JWT 토큰 추출
    private String getTokenFromCookie(HttpServletRequest request) {
        if (request.getCookies() == null) {
            return null;
        }
        for (Cookie cookie : request.getCookies()) {
            if ("AUTH_TOKEN".equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return null;
    }

    //auth token에 로그인한 회원의 학번, role 정보 저장
    private UsernamePasswordAuthenticationToken createAuthToken(String token) {
        String studentNumber = jwtUtil.getStudentNumber(token);
        String role = jwtUtil.getRole(token);

        Member member = new Member(studentNumber, Role.fromROLE_String(role));
        CustomUserDetails customUserDetails = new CustomUserDetails(member);

        return new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());
    }
}
