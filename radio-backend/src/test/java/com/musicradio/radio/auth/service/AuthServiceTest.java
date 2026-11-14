package com.musicradio.radio.auth.service;

import com.musicradio.radio.auth.dto.AdminUserResponse;
import com.musicradio.radio.auth.dto.LoginRequest;
import com.musicradio.radio.auth.dto.LoginResponse;
import com.musicradio.radio.auth.entity.AdminStatus;
import com.musicradio.radio.auth.entity.AdminUser;
import com.musicradio.radio.auth.repository.AdminUserRepository;
import com.musicradio.radio.auth.security.JwtTokenProvider;
import com.musicradio.radio.auth.service.impl.AuthServiceImpl;
import com.musicradio.radio.common.exception.ResourceNotFoundException;
import com.musicradio.radio.common.exception.UnauthorizedException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private AdminUserRepository adminUserRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @InjectMocks
    private AuthServiceImpl authService;

    private AdminUser activeAdmin;
    private AdminUser inactiveAdmin;

    @BeforeEach
    void setUp() {
        activeAdmin = AdminUser.builder()
                .id(1)
                .email("admin@musicradio.local")
                .fullName("System Administrator")
                .passwordHash("encoded_secret_hash")
                .status(AdminStatus.ACTIVE)
                .build();

        inactiveAdmin = AdminUser.builder()
                .id(2)
                .email("inactive@musicradio.local")
                .fullName("Inactive Admin")
                .passwordHash("encoded_secret_hash")
                .status(AdminStatus.INACTIVE)
                .build();
    }

    @Test
    @DisplayName("login with valid credentials should return LoginResponse with token")
    void login_WithValidCredentials_ShouldReturnLoginResponse() {
        LoginRequest request = LoginRequest.builder()
                .email("admin@musicradio.local")
                .password("Admin@123456")
                .build();

        when(adminUserRepository.findByEmail("admin@musicradio.local")).thenReturn(Optional.of(activeAdmin));
        when(passwordEncoder.matches("Admin@123456", "encoded_secret_hash")).thenReturn(true);
        when(jwtTokenProvider.generateToken(activeAdmin)).thenReturn("mocked.jwt.token");

        LoginResponse response = authService.login(request);

        assertThat(response).isNotNull();
        assertThat(response.getToken()).isEqualTo("mocked.jwt.token");
        assertThat(response.getTokenType()).isEqualTo("Bearer");
        assertThat(response.getAdmin().getEmail()).isEqualTo("admin@musicradio.local");
        assertThat(response.getAdmin().getFullName()).isEqualTo("System Administrator");
    }

    @Test
    @DisplayName("login with non-existent email should throw UnauthorizedException")
    void login_WithNonExistentEmail_ShouldThrowUnauthorizedException() {
        LoginRequest request = LoginRequest.builder()
                .email("unknown@musicradio.local")
                .password("Password@123")
                .build();

        when(adminUserRepository.findByEmail("unknown@musicradio.local")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessage("Invalid email or password");
    }

    @Test
    @DisplayName("login with incorrect password should throw UnauthorizedException")
    void login_WithWrongPassword_ShouldThrowUnauthorizedException() {
        LoginRequest request = LoginRequest.builder()
                .email("admin@musicradio.local")
                .password("WrongPassword")
                .build();

        when(adminUserRepository.findByEmail("admin@musicradio.local")).thenReturn(Optional.of(activeAdmin));
        when(passwordEncoder.matches("WrongPassword", "encoded_secret_hash")).thenReturn(false);

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessage("Invalid email or password");
    }

    @Test
    @DisplayName("login with inactive admin should throw UnauthorizedException")
    void login_WithInactiveAdmin_ShouldThrowUnauthorizedException() {
        LoginRequest request = LoginRequest.builder()
                .email("inactive@musicradio.local")
                .password("Admin@123456")
                .build();

        when(adminUserRepository.findByEmail("inactive@musicradio.local")).thenReturn(Optional.of(inactiveAdmin));
        when(passwordEncoder.matches("Admin@123456", "encoded_secret_hash")).thenReturn(true);

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessage("Admin account is inactive. Please contact administrator.");
    }

    @Test
    @DisplayName("getCurrentAdmin with valid email should return AdminUserResponse")
    void getCurrentAdmin_WithValidEmail_ShouldReturnProfile() {
        when(adminUserRepository.findByEmail("admin@musicradio.local")).thenReturn(Optional.of(activeAdmin));

        AdminUserResponse response = authService.getCurrentAdmin("admin@musicradio.local");

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1);
        assertThat(response.getEmail()).isEqualTo("admin@musicradio.local");
        assertThat(response.getStatus()).isEqualTo(AdminStatus.ACTIVE);
    }

    @Test
    @DisplayName("getCurrentAdmin with unknown email should throw ResourceNotFoundException")
    void getCurrentAdmin_WithUnknownEmail_ShouldThrowResourceNotFoundException() {
        when(adminUserRepository.findByEmail("unknown@musicradio.local")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.getCurrentAdmin("unknown@musicradio.local"))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("getCurrentAdmin with inactive admin should throw UnauthorizedException")
    void getCurrentAdmin_WithInactiveAdmin_ShouldThrowUnauthorizedException() {
        when(adminUserRepository.findByEmail("inactive@musicradio.local")).thenReturn(Optional.of(inactiveAdmin));

        assertThatThrownBy(() -> authService.getCurrentAdmin("inactive@musicradio.local"))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessage("Admin account is inactive");
    }
}
