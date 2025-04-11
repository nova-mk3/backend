package org.nova.backend.shared.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    // 게시판 전용 API 그룹
    @Bean
    public GroupedOpenApi boardApi() {
        return GroupedOpenApi.builder()
                .group("Board API")
                .pathsToMatch(
                        "/api/v1/posts/**",
                        "/api/v1/boards/**",
                        "/api/v1/comments/**",
                        "/api/v1/files/**",
                        "/api/v1/suggestions/**",
                        "/api/v1/suggestion-files"
                )
                .build();
    }

    // 관리자 전용 API 그룹
    @Bean
    public GroupedOpenApi adminApi() {
        return GroupedOpenApi.builder()
                .group("Admin API")
                .pathsToMatch(
                        "/api/v1/admin/**",
                        "/api/v1/executive-histories/**",
                        "/api/v1/pending-members/**"
                )
                .build();
    }

    // 사용자 전용 API 그룹 (회원)
    @Bean
    public GroupedOpenApi userApi() {
        return GroupedOpenApi.builder()
                .group("User API")
                .pathsToMatch(
                        "/api/v1/members/**",
                        "/api/v1/email-auth/**"
                )
                .build();
    }

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Nova API Document")
                        .description("CBNU SW 학술 동아리 Nova 홈페이지의 API 명세서")
                        .version("v1"));
    }
}