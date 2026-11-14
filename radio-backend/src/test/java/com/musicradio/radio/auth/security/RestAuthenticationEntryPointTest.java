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
import org.springframework.security.authentication.BadCredentialsException;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RestAuthenticationEntryPointTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @InjectMocks
    private RestAuthenticationEntryPoint entryPoint;

    @Test
    @DisplayName("commence writes 401 UNAUTHORIZED status and standardized JSON ApiResponse")
    void commence_ShouldWrite401JsonEnvelope() throws IOException {
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);

        when(request.getRequestURI()).thenReturn("/api/v1/admin/themes");
        when(response.getWriter()).thenReturn(printWriter);

        entryPoint.commence(request, response, new BadCredentialsException("Full authentication is required"));

        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        verify(response).setContentType(MediaType.APPLICATION_JSON_VALUE);

        String jsonOutput = stringWriter.toString();
        assertThat(jsonOutput).contains("\"success\":false");
        assertThat(jsonOutput).contains("\"errorCode\":\"UNAUTHORIZED\"");
        assertThat(jsonOutput).contains("Authentication required to access this resource");
    }
}
