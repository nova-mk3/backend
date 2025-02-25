package org.nova.backend.shared.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final SwaggerCookieInterceptor swaggerCookieInterceptor;

    public WebConfig(SwaggerCookieInterceptor swaggerCookieInterceptor) {
        this.swaggerCookieInterceptor = swaggerCookieInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(swaggerCookieInterceptor)
                .addPathPatterns("/swagger-ui/**", "/v3/api-docs/**");
    }
}