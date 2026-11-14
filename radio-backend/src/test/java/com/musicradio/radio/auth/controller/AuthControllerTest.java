package com.musicradio.radio.auth.controller;

import com.musicradio.radio.auth.dto.AdminUserResponse;
import com.musicradio.radio.auth.dto.LoginRequest;
import com.musicradio.radio.auth.dto.LoginResponse;
import com.musicradio.radio.auth.entity.AdminStatus;
import com.musicradio.radio.auth.entity.AdminUser;
import com.musicradio.radio.auth.security.CustomAdminDetails;
import com.musicradio.radio.auth.service.AuthService;
import com.musicradio.radio.common.exception.UnauthorizedException;
import com.musicradio.radio.common.response.ApiResponse;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthService authService;

    @Mock
    private HttpServletResponse response;

    @InjectMocks
    private AuthController authController;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(authController, "expirationMs", 86400000L);
        ReflectionTestUtils.setField(authController, "cookieSecure", false);
        ReflectionTestUtils.setField(authController, "cookieSameSite", "Lax");
    }

    @Test
    @DisplayName("login with valid request returns 200 OK and sets access_token cookie")
    void login_ShouldReturnOkWithCookieAndBody() {
        LoginRequest request = LoginRequest.builder()
                .email("admin@musicradio.local")
                .password("Admin@123456")
                .build();

        AdminUserResponse adminResponse = AdminUserResponse.builder()
                .id(1)
                .email("admin@musicradio.local")
                .fullName("System Administrator")
                .status(AdminStatus.ACTIVE)
                .build();

        LoginResponse loginResponse = LoginResponse.builder()
                .token("jwt.bearer.token")
                .tokenType("Bearer")
                .admin(adminResponse)
                .build();

        when(authService.login(request)).thenReturn(loginResponse);

        ResponseEntity<ApiResponse<LoginResponse>> result = authController.login(request, response);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isNotNull();
        assertThat(result.getBody().isSuccess()).isTrue();
        assertThat(result.getBody().getData().getToken()).isEqualTo("jwt.bearer.token");

        ArgumentCaptor<String> headerCaptor = ArgumentCaptor.forClass(String.class);
        verify(response).addHeader(eq(HttpHeaders.SET_COOKIE), headerCaptor.capture());
        assertThat(headerCaptor.getValue()).contains("access_token=jwt.bearer.token");
        assertThat(headerCaptor.getValue()).contains("HttpOnly");
    }

    @Test
    @DisplayName("logout returns 200 OK and emits clean cookie with maxAge=0")
    void logout_ShouldReturnOkWithCleanCookie() {
        ResponseEntity<ApiResponse<Void>> result = authController.logout(response);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isNotNull();
        assertThat(result.getBody().isSuccess()).isTrue();
        assertThat(result.getBody().getMessage()).isEqualTo("Logout successful");

        ArgumentCaptor<String> headerCaptor = ArgumentCaptor.forClass(String.class);
        verify(response).addHeader(eq(HttpHeaders.SET_COOKIE), headerCaptor.capture());
        assertThat(headerCaptor.getValue()).contains("access_token=");
        assertThat(headerCaptor.getValue()).contains("Max-Age=0");
    }

    @Test
    @DisplayName("getCurrentAdmin with authenticated principal returns 200 OK and admin profile")
    void getCurrentAdmin_WhenAuthenticated_ShouldReturnAdminProfile() {
        AdminUser admin = AdminUser.builder()
                .id(1)
                .email("admin@musicradio.local")
                .fullName("System Administrator")
                .status(AdminStatus.ACTIVE)
                .build();
        CustomAdminDetails principal = new CustomAdminDetails(admin);

        AdminUserResponse profile = AdminUserResponse.builder()
                .id(1)
                .email("admin@musicradio.local")
                .fullName("System Administrator")
                .status(AdminStatus.ACTIVE)
                .build();

        when(authService.getCurrentAdmin("admin@musicradio.local")).thenReturn(profile);

        ResponseEntity<ApiResponse<AdminUserResponse>> result = authController.getCurrentAdmin(principal);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isNotNull();
        assertThat(result.getBody().getData().getEmail()).isEqualTo("admin@musicradio.local");
    }

    @Test
    @DisplayName("getCurrentAdmin with null principal throws UnauthorizedException")
    void getCurrentAdmin_WhenPrincipalNull_ShouldThrowUnauthorizedException() {
        assertThatThrownBy(() -> authController.getCurrentAdmin(null))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessage("Not authenticated");
    }
}
