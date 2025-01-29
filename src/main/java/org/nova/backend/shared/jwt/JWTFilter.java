package org.nova.backend.shared.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
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

        String authorization = request.getHeader("Authorization");

        if(!checkAuthorizationHeader(authorization)){
            filterChain.doFilter(request, response);
            return;
        }

        String token = authorization.split(" ")[1];

        if(checkExpiredToken(token)){
            filterChain.doFilter(request, response);
            return;
        }

        UsernamePasswordAuthenticationToken authToken = createAuthToken(token);

        SecurityContextHolder.getContext().setAuthentication(authToken);

        filterChain.doFilter(request, response);
    }

    // Authorization 헤더 검증
    private boolean checkAuthorizationHeader(String authorization) {
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            log.info("token null");
            return false;
        }
        return true;
    }

    //토큰 소멸 시간 검증
    private boolean checkExpiredToken(String token) {
        if (jwtUtil.isExpired(token)) {
            log.info("token expired");
            return true;
        }
        return false;
    }

    //auth token에 로그인한 회원의 학번, role 정보 저장
    private UsernamePasswordAuthenticationToken createAuthToken(String token){
        String studentNumber = jwtUtil.getStudentNumber(token);
        String role = jwtUtil.getRole(token);

        Member member = new Member(studentNumber, Role.fromROLE_String(role));
        CustomUserDetails customUserDetails = new CustomUserDetails(member);

        return new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());
    }
}
