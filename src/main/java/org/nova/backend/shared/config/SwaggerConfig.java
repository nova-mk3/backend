package org.nova.backend.shared.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {

        Info info = new Info()
                .title("Nova API Document")
                .description("CBNU SW 학술 동아리 Nova 홈페이지의 API 명세서")
                .version("v1");

        return new OpenAPI()
                .info(info);
    }
}
