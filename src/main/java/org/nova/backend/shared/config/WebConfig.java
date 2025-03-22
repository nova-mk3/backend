package org.nova.backend.shared.config;

import org.nova.backend.shared.constants.FilePathConstants;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration(proxyBeanMethods = false)
public class WebConfig implements WebMvcConfigurer {

    private final SwaggerCookieInterceptor swaggerCookieInterceptor;

    @Value("${file.storage.path}")
    private String fileStoragePath;

    public WebConfig(SwaggerCookieInterceptor swaggerCookieInterceptor) {
        this.swaggerCookieInterceptor = swaggerCookieInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(swaggerCookieInterceptor)
                .addPathPatterns("/swagger-ui/**", "/v3/api-docs/**");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry
                .addResourceHandler(FilePathConstants.PUBLIC_FILE_URL_PREFIX + "**")
                .addResourceLocations("file:" + fileStoragePath + "/" + FilePathConstants.PUBLIC_FOLDER + "/");
    }
}