package com.musicradio.radio.auth.controller;

import com.musicradio.radio.auth.dto.AdminUserResponse;
import com.musicradio.radio.auth.dto.LoginRequest;
import com.musicradio.radio.auth.dto.LoginResponse;
import com.musicradio.radio.auth.security.CustomAdminDetails;
import com.musicradio.radio.auth.service.AuthService;
import com.musicradio.radio.auth.util.CookieUtils;
import com.musicradio.radio.common.exception.UnauthorizedException;
import com.musicradio.radio.common.response.ApiResponse;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller handling administrator authentication: login, logout, and profile retrieval.
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @Value("${jwt.expiration-ms:86400000}")
    private long expirationMs;

    @Value("${jwt.cookie-secure:false}")
    private boolean cookieSecure;

    @Value("${jwt.cookie-same-site:Lax}")
    private String cookieSameSite;

    /**
     * Authenticate administrator credentials, emit an HTTP-only SameSite cookie, and return Bearer token.
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletResponse response) {
        log.info("Processing login attempt for email: {}", request.getEmail());
        LoginResponse loginResponse = authService.login(request);

        ResponseCookie cookie = CookieUtils.createAccessTokenCookie(
                loginResponse.getToken(),
                expirationMs / 1000,
                cookieSecure,
                cookieSameSite
        );
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        return ResponseEntity.ok(ApiResponse.success(loginResponse, "Login successful"));
    }

    /**
     * Clear administrator authentication cookie and invalidate client session.
     */
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(HttpServletResponse response) {
        log.info("Processing administrator logout");
        ResponseCookie cleanCookie = CookieUtils.createCleanAccessTokenCookie(cookieSecure, cookieSameSite);
        response.addHeader(HttpHeaders.SET_COOKIE, cleanCookie.toString());

        return ResponseEntity.ok(ApiResponse.success(null, "Logout successful"));
    }

    /**
     * Retrieve the sanitized profile of the currently authenticated administrator.
     */
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<AdminUserResponse>> getCurrentAdmin(
            @AuthenticationPrincipal CustomAdminDetails principal) {
        if (principal == null) {
            throw new UnauthorizedException("Not authenticated");
        }

        AdminUserResponse adminResponse = authService.getCurrentAdmin(principal.getUsername());
        return ResponseEntity.ok(ApiResponse.success(adminResponse, "Current admin profile retrieved"));
    }
}
