package com.musicradio.radio.auth.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseCookie;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CookieUtilsTest {

    @Mock
    private HttpServletRequest request;

    @Test
    @DisplayName("createAccessTokenCookie should configure correct attributes")
    void createAccessTokenCookie_ShouldConfigureCorrectAttributes() {
        ResponseCookie cookie = CookieUtils.createAccessTokenCookie("test-token-value", 3600, false, "Lax");

        assertThat(cookie.getName()).isEqualTo(CookieUtils.ACCESS_TOKEN_COOKIE_NAME);
        assertThat(cookie.getValue()).isEqualTo("test-token-value");
        assertThat(cookie.isHttpOnly()).isTrue();
        assertThat(cookie.isSecure()).isFalse();
        assertThat(cookie.getPath()).isEqualTo("/");
        assertThat(cookie.getMaxAge().getSeconds()).isEqualTo(3600);
        assertThat(cookie.getSameSite()).isEqualTo("Lax");
    }

    @Test
    @DisplayName("createCleanAccessTokenCookie should configure zero max age and empty value")
    void createCleanAccessTokenCookie_ShouldConfigureZeroMaxAge() {
        ResponseCookie cookie = CookieUtils.createCleanAccessTokenCookie(false, "Lax");

        assertThat(cookie.getName()).isEqualTo(CookieUtils.ACCESS_TOKEN_COOKIE_NAME);
        assertThat(cookie.getValue()).isEmpty();
        assertThat(cookie.isHttpOnly()).isTrue();
        assertThat(cookie.getMaxAge().getSeconds()).isEqualTo(0);
        assertThat(cookie.getPath()).isEqualTo("/");
    }

    @Test
    @DisplayName("extractTokenFromCookie should return token when matching cookie exists")
    void extractTokenFromCookie_WhenPresent_ShouldReturnToken() {
        Cookie[] cookies = new Cookie[]{
                new Cookie("other_cookie", "other_value"),
                new Cookie(CookieUtils.ACCESS_TOKEN_COOKIE_NAME, "expected-jwt-token")
        };
        when(request.getCookies()).thenReturn(cookies);

        Optional<String> token = CookieUtils.extractTokenFromCookie(request, CookieUtils.ACCESS_TOKEN_COOKIE_NAME);

        assertThat(token).isPresent().contains("expected-jwt-token");
    }

    @Test
    @DisplayName("extractTokenFromCookie should return empty when cookie is absent")
    void extractTokenFromCookie_WhenAbsent_ShouldReturnEmpty() {
        Cookie[] cookies = new Cookie[]{
                new Cookie("other_cookie", "other_value")
        };
        when(request.getCookies()).thenReturn(cookies);

        Optional<String> token = CookieUtils.extractTokenFromCookie(request, CookieUtils.ACCESS_TOKEN_COOKIE_NAME);

        assertThat(token).isEmpty();
    }

    @Test
    @DisplayName("extractTokenFromCookie should return empty when cookies array is null")
    void extractTokenFromCookie_WhenCookiesNull_ShouldReturnEmpty() {
        when(request.getCookies()).thenReturn(null);

        Optional<String> token = CookieUtils.extractTokenFromCookie(request, CookieUtils.ACCESS_TOKEN_COOKIE_NAME);

        assertThat(token).isEmpty();
    }
}
