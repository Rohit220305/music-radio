package com.musicradio.radio.auth.service;

import com.musicradio.radio.auth.dto.AdminUserResponse;
import com.musicradio.radio.auth.dto.LoginRequest;
import com.musicradio.radio.auth.dto.LoginResponse;

/**
 * Service contract for administrator authentication and profile retrieval.
 */
public interface AuthService {

    /**
     * Authenticate an admin user with email and password credentials.
     *
     * @param request Login credentials
     * @return LoginResponse containing signed Bearer JWT and user profile
     */
    LoginResponse login(LoginRequest request);

    /**
     * Retrieve the authenticated admin user's sanitized profile by email.
     *
     * @param email Administrator's unique email address
     * @return AdminUserResponse
     */
    AdminUserResponse getCurrentAdmin(String email);
}
