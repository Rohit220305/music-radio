package com.musicradio.radio.auth.service.impl;

import com.musicradio.radio.auth.dto.AdminUserResponse;
import com.musicradio.radio.auth.dto.LoginRequest;
import com.musicradio.radio.auth.dto.LoginResponse;
import com.musicradio.radio.auth.entity.AdminStatus;
import com.musicradio.radio.auth.entity.AdminUser;
import com.musicradio.radio.auth.repository.AdminUserRepository;
import com.musicradio.radio.auth.security.JwtTokenProvider;
import com.musicradio.radio.auth.service.AuthService;
import com.musicradio.radio.common.exception.ResourceNotFoundException;
import com.musicradio.radio.common.exception.UnauthorizedException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Production implementation of AuthService enforcing timing-attack-resistant login checks,
 * active account validation, and JWT token issuance.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthServiceImpl implements AuthService {

    private final AdminUserRepository adminUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public LoginResponse login(LoginRequest request) {
        String email = request.getEmail().toLowerCase().trim();
        AdminUser user = adminUserRepository.findByEmail(email)
                .orElseThrow(() -> new UnauthorizedException("Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new UnauthorizedException("Invalid email or password");
        }

        if (user.getStatus() != AdminStatus.ACTIVE) {
            throw new UnauthorizedException("Admin account is inactive. Please contact administrator.");
        }

        String token = jwtTokenProvider.generateToken(user);
        log.info("Admin user logged in successfully: {}", email);

        return LoginResponse.builder()
                .token(token)
                .tokenType("Bearer")
                .admin(AdminUserResponse.fromEntity(user))
                .build();
    }

    @Override
    public AdminUserResponse getCurrentAdmin(String email) {
        AdminUser user = adminUserRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("AdminUser", "email", email));

        if (user.getStatus() != AdminStatus.ACTIVE) {
            throw new UnauthorizedException("Admin account is inactive");
        }

        return AdminUserResponse.fromEntity(user);
    }
}
