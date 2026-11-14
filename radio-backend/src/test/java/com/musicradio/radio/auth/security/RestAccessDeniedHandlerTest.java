package com.musicradio.radio.auth.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RestAccessDeniedHandlerTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @InjectMocks
    private RestAccessDeniedHandler accessDeniedHandler;

    @Test
    @DisplayName("handle writes 403 FORBIDDEN status and standardized JSON ApiResponse")
    void handle_ShouldWrite403JsonEnvelope() throws IOException {
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);

        when(request.getRequestURI()).thenReturn("/api/v1/admin/dashboard/stats");
        when(response.getWriter()).thenReturn(printWriter);

        accessDeniedHandler.handle(request, response, new AccessDeniedException("Access is denied"));

        verify(response).setStatus(HttpServletResponse.SC_FORBIDDEN);
        verify(response).setContentType(MediaType.APPLICATION_JSON_VALUE);

        String jsonOutput = stringWriter.toString();
        assertThat(jsonOutput).contains("\"success\":false");
        assertThat(jsonOutput).contains("\"errorCode\":\"FORBIDDEN\"");
        assertThat(jsonOutput).contains("Access denied: insufficient privileges");
    }
}
