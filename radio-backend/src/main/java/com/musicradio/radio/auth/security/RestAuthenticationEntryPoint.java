package com.musicradio.radio.auth.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;

/**
 * Authentication entry point invoked when an unauthenticated client attempts
 * to access a protected administrative resource. Writes a standardized 401 ApiResponse.
 */
@Slf4j
@Component
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        log.warn("Unauthorized access attempt to {}: {}", request.getRequestURI(), authException.getMessage());

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());

        String jsonResponse = String.format(
                "{\"success\":false,\"message\":\"Authentication required to access this resource\",\"data\":null,\"errorCode\":\"UNAUTHORIZED\",\"errors\":null,\"timestamp\":\"%s\"}",
                Instant.now()
        );

        response.getWriter().write(jsonResponse);
    }
}
