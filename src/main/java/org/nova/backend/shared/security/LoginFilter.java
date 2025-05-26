package org.nova.backend.shared.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import lombok.extern.slf4j.Slf4j;
import org.nova.backend.auth.application.dto.dto.AuthLoginDTO;
import org.nova.backend.auth.application.dto.dto.CustomUserDetails;
import org.nova.backend.shared.jwt.JWTUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.util.StreamUtils;

@Slf4j
public class LoginFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final JWTUtil jwtUtil;
    private final boolean isSecureCookie;

    public LoginFilter(
            AuthenticationManager authenticationManager,
            JWTUtil jwtUtil,
            boolean isSecureCookie
    ) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.isSecureCookie = isSecureCookie;

        setFilterProcessesUrl("/api/v1/members/login");
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {

        log.info("인증 시도");

        AuthLoginDTO loginDTO = getLoginDataFromHTTPRequest(request);

        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                loginDTO.getStudentNumber(), loginDTO.getPassword(), null);

        return authenticationManager.authenticate(authToken);
    }

    private AuthLoginDTO getLoginDataFromHTTPRequest(HttpServletRequest request) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            ServletInputStream inputStream = request.getInputStream();
            String messageBody = StreamUtils.copyToString(inputStream, StandardCharsets.UTF_8);

            return objectMapper.readValue(messageBody, AuthLoginDTO.class);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    protected void successfulAuthentication(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain chain,
            Authentication authentication
    ) throws IOException {
        log.info("로그인 성공");

        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        String token = createAuthorizationToken(authentication);

        // JSON 응답 추가
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        boolean isTempPassword = customUserDetails.isTempPassword();

        String jsonResponse = String.format("""
        {
            "status": 200,
            "message": "success",
            "data": {
                "message": "로그인 성공",
                "tempPassword": %s
            }
        }
        """, isTempPassword);

        //헤더 설정
        Cookie cookie = new Cookie("AUTH_TOKEN", token);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(60 * 60 * 5); // 5시간

        response.setHeader("Set-Cookie", String.format(
                "AUTH_TOKEN=%s; Path=/; Max-Age=%d; HttpOnly; SameSite=Strict%s",
                token, 60 * 60 * 5,
                isSecureCookie ? "; Secure" : ""
        ));

        response.getWriter().write(jsonResponse);
    }

    /*
    로그인 인증 토큰 생성 : 학번, 이름, role 저장
     */
    private String createAuthorizationToken(Authentication authentication) {
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();

        //토큰, 세션에 학번, 이름, role 저장
        String studentNumber = customUserDetails.getStudentNumber();
        String name = customUserDetails.getUsername();
        String role = SecurityUtils.getRole(authentication);

        return jwtUtil.createJwt(studentNumber, name, role, 60 * 60 * 60 * 10L);
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                              AuthenticationException failed) {
        response.setStatus(401);
    }
}
