package com.musicradio.radio.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Interim security configuration active ONLY for development and test profiles.
 * Temporarily permits all requests to /api/v1/** so Phase 3 APIs can be exercised without credentials.
 *
 * WARNING: Administrative endpoints (/api/v1/admin/**) are temporarily unauthenticated and unsecured
 * under these profiles. In production (prod profile), this configuration will not load.
 * This configuration will be replaced in Phase 4 by production JWT authentication, HTTP-only cookie parsing,
 * BCrypt password hashing, and role-based guards.
 */
@Configuration
@EnableWebSecurity
@Profile({"dev", "test"})
public class InterimSecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/v1/**").permitAll()
                .anyRequest().permitAll()
            );
        return http.build();
    }
}

