package org.nova.backend.shared.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Set;
import java.util.logging.Filter;
import java.util.logging.LogRecord;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CORSFilter implements Filter, jakarta.servlet.Filter {

    private static final Set<String> ALLOWED_ORIGINS = Set.of(
            "https://jinybook.site",
            "http://localhost:8080",
            "http://localhost:3000",
            "http://localhost:3001",
            "http://localhost:3002"
    );

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        jakarta.servlet.Filter.super.init(filterConfig);
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        String origin = request.getHeader("Origin");

        if (isSafeOrigin(origin) && ALLOWED_ORIGINS.contains(origin)) {
            response.setHeader("Access-Control-Allow-Origin", origin);
        }

        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");
        response.setHeader("Access-Control-Allow-Credentials", "true");

        // pre-flight (OPTIONS) 요청일 경우, 여기서 바로 응답 처리
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            response.setStatus(HttpServletResponse.SC_OK);
            return;
        }

        chain.doFilter(req, res);
    }

    private boolean isSafeOrigin(String origin) {
        return origin != null && !origin.contains("\r") && !origin.contains("\n");
    }

    @Override
    public boolean isLoggable(LogRecord record) {
        return false;
    }
}