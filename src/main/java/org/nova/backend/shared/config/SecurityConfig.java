package org.nova.backend.shared.config;

import lombok.RequiredArgsConstructor;
import org.nova.backend.member.domain.model.valueobject.Role;
import org.nova.backend.shared.jwt.JWTFilter;
import org.nova.backend.shared.jwt.JWTUtil;
import org.nova.backend.shared.security.LoginFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final AuthenticationConfiguration authenticationConfiguration;
    private final JWTUtil jwtUtil;

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {

        return configuration.getAuthenticationManager();
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {

        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .csrf(AbstractHttpConfigurer::disable);

        http
                .formLogin(AbstractHttpConfigurer::disable);

        http
                .httpBasic(AbstractHttpConfigurer::disable);

        http
                .authorizeHttpRequests((auth) -> {
                    //건의 게시판 관련 권한
                    configureSuggestionBoardPermissions(auth);
                    //게시판 관련 권한
                    configureBoardPermissions(auth);
                    //족보 게시판 관련 권한
                    configureJokboBoardPermissions(auth);
                    //댓글 관련 권한
                    configureCommentPermissions(auth);
                    //로그인 회원가입 관련 권한
                    configureAuthPermissions(auth);
                    //관리자 관련 권한
                    configureAdministratorPermissions(auth);

                    auth.requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html").permitAll()
                            .requestMatchers("/", "/api/v1", "/service/**").permitAll()
                            .requestMatchers("/api/v1/comments/**").permitAll()
                            .anyRequest().authenticated();
                });

        http
                .addFilterBefore(new JWTFilter(jwtUtil), LoginFilter.class);

        http
                .addFilterAt(new LoginFilter(authenticationManager(authenticationConfiguration), jwtUtil),
                        UsernamePasswordAuthenticationFilter.class);

        http
                .sessionManagement((session) -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }

    private void configureSuggestionBoardPermissions(
            AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry auth
    ) {
        auth
                // 건의 게시글 조회는 누구나 가능 (단, 비공개 게시글은 사용자 본인 또는 관리자만 볼 수 있음)
                .requestMatchers(HttpMethod.GET, "/api/v1/suggestions").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/v1/suggestions/{postId}").permitAll()

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
                        "/api/v1/boards/{boardId}/posts/{postId}",
                        "/api/v1/boards/{boardId}/posts/latest",
                        "/api/v1/boards/{boardId}/posts/all",
                        "/api/v1/boards/{boardId}/posts/search",
                        "/api/v1/boards/{boardId}/posts/all/search"
                ).permitAll()

                // 로그인한 사용자만 접근 가능한 API (일반 게시글 작성, 수정)
                .requestMatchers(HttpMethod.POST, "/api/v1/boards/{boardId}/posts")
                .authenticated()

                // 공지사항 게시판의 게시글 작성 & 수정 (관리자 & 회장만)
                .requestMatchers(HttpMethod.POST, "/api/v1/boards/{boardId}/posts")
                .hasAnyRole(Role.ADMINISTRATOR.toString(), Role.CHAIRMAN.toString());
    }

    private void configureJokboBoardPermissions(
            AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry auth
    ) {
        auth
                // 로그인 없이 접근 가능한 API
                .requestMatchers(
                        "/api/v1/boards/{boardId}/exam-posts",
                        "/api/v1/boards/{boardId}/exam-posts/{postId}"
                ).permitAll()

                // 로그인한 사용자만 접근 가능한 API (일반 게시글 작성, 수정)
                .requestMatchers(HttpMethod.POST, "/api/v1/boards/{boardId}/exam-posts/posts")
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

//    private void configureAdministratorPermissions(
//            AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry auth
//    ) {
//        auth.requestMatchers("/api/v1/admin").permitAll()
//                //.hasRole(Role.ADMINISTRATOR.toString())  //ROLE_ 접두사를 붙여서 권한을 확인한다.
//                .requestMatchers("/api/v1/pendingMembers/**").permitAll()
//                .requestMatchers("/api/v1/executiveHistories/**")
//                .permitAll();
//    }

    private void configureAdministratorPermissions(
            AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry auth
    ) {
        auth.requestMatchers("/api/v1/admin")
                .hasRole(Role.ADMINISTRATOR.toString())  //ROLE_ 접두사를 붙여서 권한을 확인한다.
                .requestMatchers("/api/v1/pendingMembers/**").hasRole(Role.ADMINISTRATOR.toString())
                .requestMatchers("/api/v1/executiveHistories/**")
                .hasRole(Role.ADMINISTRATOR.toString());
    }

    private void configureAuthPermissions(
            AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry auth
    ) {
        auth
                // 회원가입, 로그인
                .requestMatchers("/api/v1/members", "/api/v1/members/login").permitAll()
                // 회원가입 시 이메일 인증
                .requestMatchers("/api/v1/email-auth/**").permitAll();
    }
}