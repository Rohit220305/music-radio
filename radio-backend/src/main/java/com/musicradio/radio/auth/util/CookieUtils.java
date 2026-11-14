package com.musicradio.radio.auth.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseCookie;

import java.util.Arrays;
import java.util.Optional;

/**
 * Utility helper class for building and clearing secure HTTP-only cookies and extracting tokens from requests.
 */
public final class CookieUtils {

    public static final String ACCESS_TOKEN_COOKIE_NAME = "access_token";

    private CookieUtils() {
    }

    /**
     * Create an HTTP-only SameSite access token cookie.
     */
    public static ResponseCookie createAccessTokenCookie(String token, long maxAgeSeconds, boolean secure, String sameSite) {
        return ResponseCookie.from(ACCESS_TOKEN_COOKIE_NAME, token)
                .httpOnly(true)
                .secure(secure)
                .path("/")
                .maxAge(maxAgeSeconds)
                .sameSite(sameSite)
                .build();
    }

    /**
     * Create an immediate expiration cookie to clear the access token in browser clients.
     */
    public static ResponseCookie createCleanAccessTokenCookie(boolean secure, String sameSite) {
        return ResponseCookie.from(ACCESS_TOKEN_COOKIE_NAME, "")
                .httpOnly(true)
                .secure(secure)
                .path("/")
                .maxAge(0)
                .sameSite(sameSite)
                .build();
    }

    /**
     * Extract token string from request cookies by cookie name.
     */
    public static Optional<String> extractTokenFromCookie(HttpServletRequest request, String cookieName) {
        if (request == null || request.getCookies() == null) {
            return Optional.empty();
        }
        return Arrays.stream(request.getCookies())
                .filter(c -> cookieName.equals(c.getName()))
                .map(Cookie::getValue)
                .filter(v -> v != null && !v.isBlank())
                .findFirst();
    }
}
