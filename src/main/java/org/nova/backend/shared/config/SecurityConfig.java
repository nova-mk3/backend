package org.nova.backend.shared.config;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.nova.backend.member.domain.model.valueobject.Role;
import org.nova.backend.shared.jwt.JWTFilter;
import org.nova.backend.shared.jwt.JWTUtil;
import org.nova.backend.shared.security.CORSFilter;
import org.nova.backend.shared.security.LoginFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.Set;

@Configuration(proxyBeanMethods = false)
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private static final String POST_BASE = "/api/v1/boards/{boardId}/posts";
    private final AuthenticationConfiguration authenticationConfiguration;
    private final JWTUtil jwtUtil;

    @Value("${cookie.secure}")
    private boolean isSecureCookie;

    @Bean
    public AuthenticationManager authenticationManager() throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {

        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(
            HttpSecurity http,
            AuthenticationManager authManager
    ) throws Exception {

        http
                .csrf(AbstractHttpConfigurer::disable);

        http
                .formLogin(AbstractHttpConfigurer::disable);

        http
                .httpBasic(AbstractHttpConfigurer::disable);

        http
                .authorizeHttpRequests(auth -> {
                    auth.requestMatchers("/files/public/**").permitAll();

                    //건의 게시판 관련 권한
                    configureSuggestionBoardPermissions(auth);
                    //게시판 관련 권한
                    configureBoardPermissions(auth);
                    //족보 게시판 관련 권한
                    configureJokboBoardPermissions(auth);
                    //사진 게시판 관련 권한
                    configurePictureBoardPermissions(auth);
                    //댓글 관련 권한
                    configureCommentPermissions(auth);
                    //로그인 회원가입, 사용자 정보 관련 권한
                    configureAuthPermissions(auth);
                    //관리자 관련 권한
                    configureAdministratorPermissions(auth);
                    auth.requestMatchers("/actuator/health", "/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html")
                            .permitAll()
                            .requestMatchers("/", "/api/v1", "/service/**").permitAll()
                            .anyRequest().authenticated();
                });

        http
                .logout(this::logOut);

        http
                .addFilterBefore(new CORSFilter(), LoginFilter.class);
        http
                .addFilterBefore(new JWTFilter(jwtUtil), LoginFilter.class);

        http
                .addFilterAt(
                        new LoginFilter(
                                authManager,
                                jwtUtil,
                                isSecureCookie
                        ),
                        UsernamePasswordAuthenticationFilter.class);

        http
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }

    private void logOut(LogoutConfigurer<HttpSecurity> logout) {
        logout.logoutUrl("/api/v1/members/logout")
                .logoutSuccessHandler((request, response, authentication) -> {
                    // CORS 헤더 설정
                    String origin = request.getHeader("Origin");
                    if (origin != null && ALLOWED_ORIGINS.contains(origin)) {
                        response.setHeader("Access-Control-Allow-Origin", origin);
                        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
                        response.setHeader("Access-Control-Allow-Headers", "Content-Type");
                        response.setHeader("Access-Control-Allow-Credentials", "true");
                    }

                    // auth token 담은 쿠키 제거
                    Cookie cookie = new Cookie("AUTH_TOKEN", null);
                    cookie.setHttpOnly(true);
                    cookie.setPath("/");
                    cookie.setMaxAge(0);
                    response.addCookie(cookie);
                    response.setStatus(HttpServletResponse.SC_OK);
                });
    }

    // ALLOWED_ORIGINS Set 추가
    private static final Set<String> ALLOWED_ORIGINS = Set.of(
            "https://nova.cbnu.ac.kr",
            "http://localhost:8080",
            "http://localhost:3000",
            "http://localhost:3001",
            "http://localhost:3002"
    );

    private void configureSuggestionBoardPermissions(
            AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry auth
    ) {
        auth
                // 로그인 없이 접근 가능한 API
                .requestMatchers(
                        "/api/v1/suggestions",
                        "/api/v1/suggestions/search"
                ).permitAll()

                // 건의 게시글 작성 및 파일 업로드는 로그인한 사용자만 가능
                .requestMatchers(HttpMethod.POST, "/api/v1/suggestions").authenticated()
                .requestMatchers(HttpMethod.POST, "/api/v1/suggestion-files").authenticated()

                // 관리자만 답변을 작성할 수 있음
                .requestMatchers(HttpMethod.PUT, "/api/v1/suggestions/{postId}/reply")
                .hasAnyRole(Role.ADMINISTRATOR.toString());
    }

    private void configureBoardPermissions(
            AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry auth
    ) {
        auth
                // 로그인 없이 접근 가능한 API
                .requestMatchers(
                        "/api/v1/boards",
                        "/api/v1/boards/{boardId}/posts",
                        "/api/v1/boards/{boardId}/posts/latest",
                        "/api/v1/boards/{boardId}/posts/all",
                        "/api/v1/boards/{boardId}/posts/search",
                        "/api/v1/boards/{boardId}/posts/all/search",
                        "/api/v1/posts/across-boards"
                ).permitAll()

                // 로그인한 사용자만 접근 가능한 API (일반 게시글 작성, 수정)
                .requestMatchers(HttpMethod.POST, POST_BASE)
                .authenticated()

                // 공지사항 게시판의 게시글 작성 & 수정 (관리자 & 회장만)
                .requestMatchers(HttpMethod.POST, POST_BASE)
                .hasAnyRole(Role.ADMINISTRATOR.toString(), Role.CHAIRMAN.toString());
    }

    private void configureJokboBoardPermissions(
            AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry auth
    ) {
        auth
                // 로그인 없이 접근 가능한 API
                .requestMatchers(
                        "/api/v1/boards/{boardId}/exam-posts"
                ).permitAll()

                // 로그인한 사용자만 접근 가능한 API (일반 게시글 작성, 수정)
                .requestMatchers(HttpMethod.POST, "/api/v1/boards/{boardId}/exam-posts/posts")
                .authenticated();
    }

    private void configurePictureBoardPermissions(
            AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry auth
    ) {
        auth
                // 로그인 없이 접근 가능한 API
                .requestMatchers(
                        "/api/v1/boards/{boardId}/picture-posts"
                ).permitAll()

                // 로그인한 사용자만 접근 가능한 API (일반 게시글 작성, 수정)
                .requestMatchers(HttpMethod.POST, "/api/v1/boards/{boardId}/picture-posts/posts")
                .authenticated();
    }

    private void configureCommentPermissions(
            AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry auth
    ) {
        auth
                // 댓글 조회는 모든 사용자 허용
                .requestMatchers(HttpMethod.GET, "/api/v1/posts/{postId}/comments").permitAll()

                // 댓글 작성은 로그인 사용자만 가능
                .requestMatchers(HttpMethod.POST, "/api/v1/posts/{postId}/comments").authenticated();
    }

    private void configureAdministratorPermissions(
            AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry auth
    ) {
        auth.requestMatchers("/api/v1/admin/**").hasRole(Role.ADMINISTRATOR.toString())  //ROLE_ 접두사를 붙여서 권한을 확인한다.
                .requestMatchers("/api/v1/pending-members/**").hasRole(Role.ADMINISTRATOR.toString())
                .requestMatchers("/api/v1/executive-histories/**").hasRole(Role.ADMINISTRATOR.toString());
    }

    private void configureAuthPermissions(
            AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry auth
    ) {
        auth
                .requestMatchers(HttpMethod.POST, "/api/v1/members").permitAll()  //회원가입
                .requestMatchers(HttpMethod.POST, "/api/v1/members/profile-photo").permitAll()  //회원가입시 프로필 사진 업로드
                .requestMatchers(HttpMethod.POST, "/api/v1/members/reset-password").permitAll()  //비밀번호 초기화
                .requestMatchers( "/api/v1/members/simple-profile").permitAll()  //회원 간단 프로필 조회
                .requestMatchers("/api/v1/members/login").permitAll()  //로그인
                // 회원가입 시 이메일 인증
                .requestMatchers("/api/v1/email-auth/**").permitAll()
                .requestMatchers("/api/v1/members/**").authenticated();  //회원 정보 접근 권한
    }
}