package com.musicradio.radio.auth.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;

/**
 * Access denied handler invoked when an authenticated client lacks the required
 * administrative role to access a resource. Writes a standardized 403 ApiResponse.
 */
@Slf4j
@Component
public class RestAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException {
        log.warn("Access denied to {}: {}", request.getRequestURI(), accessDeniedException.getMessage());

        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());

        String jsonResponse = String.format(
                "{\"success\":false,\"message\":\"Access denied: insufficient privileges\",\"data\":null,\"errorCode\":\"FORBIDDEN\",\"errors\":null,\"timestamp\":\"%s\"}",
                Instant.now()
        );

        response.getWriter().write(jsonResponse);
    }
}
