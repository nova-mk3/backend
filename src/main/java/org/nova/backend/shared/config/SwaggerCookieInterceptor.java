package org.nova.backend.shared.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Slf4j
@Component
public class SwaggerCookieInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        Optional<String> authToken = getAuthTokenFromRequest(request);

        if (authToken.isPresent()) {
            log.info("Swagger 요청 - JWT 유지: {}", authToken.get());
            response.addHeader("Set-Cookie", "AUTH_TOKEN=" + authToken.get() + "; Path=/; HttpOnly; Secure");
        } else {
            log.warn("Swagger 요청 - JWT 없음");
        }
        return true;
    }

    private Optional<String> getAuthTokenFromRequest(HttpServletRequest request) {
        if (request.getCookies() == null) {
            return Optional.empty();
        }
        for (var cookie : request.getCookies()) {
            if ("AUTH_TOKEN".equals(cookie.getName())) {
                return Optional.ofNullable(cookie.getValue());
            }
        }
        return Optional.empty();
    }
}