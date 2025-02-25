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

        String requestURI = request.getRequestURI();

        // Authorization 헤더에서 JWT 가져오기
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7); // "Bearer " 제거
        } else {
            token = getTokenFromCookie(request); // 없으면 쿠키에서 가져오기
        }

        if (token == null || token.isEmpty()) {
            log.debug("JWT 없음 - 요청 URL: {}", requestURI);
            filterChain.doFilter(request, response);
            return;
        }

        try {
            if (jwtUtil.isExpired(token)) { //토큰 소멸 시간 검증
                log.debug("JWT 만료됨 - 요청 URL: {}", requestURI);
                SecurityContextHolder.clearContext();
                filterChain.doFilter(request, response);
                return;
            }

            UsernamePasswordAuthenticationToken authToken = createAuthToken(token);
            SecurityContextHolder.getContext().setAuthentication(authToken);

            if (log.isDebugEnabled()) {
                log.debug("JWT 인증 성공 - 사용자 ID: {}", ((CustomUserDetails) authToken.getPrincipal()).getUsername());
            }

        } catch (Exception e) {
            log.error("JWT 처리 중 예외 발생 - 요청 URL: {}", requestURI, e);
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }


    // 쿠키에서 JWT 토큰 추출
    private String getTokenFromCookie(HttpServletRequest request) {
        if (request.getCookies() == null) {
            return null;
        }
        for (Cookie cookie : request.getCookies()) {
            if ("AUTH_TOKEN".equals(cookie.getName())) {
                String token = cookie.getValue();
                return (token != null && !token.trim().isEmpty()) ? token : null;
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
