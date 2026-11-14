package com.musicradio.radio;

import com.musicradio.radio.auth.dto.AdminUserResponse;
import com.musicradio.radio.auth.entity.AdminStatus;
import com.musicradio.radio.auth.entity.AdminUser;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class AdminUserDomainTest {

    @Test
    @DisplayName("Should build AdminUser entity with Integer ID and AdminStatus enum")
    void testAdminUserEntity() {
        AdminUser user = AdminUser.builder()
                .id(1)
                .email("admin@musicradio.com")
                .passwordHash("$2a$12$e8yQ7M...")
                .fullName("System Administrator")
                .status(AdminStatus.ACTIVE)
                .build();

        assertEquals(1, user.getId());
        assertEquals("admin@musicradio.com", user.getEmail());
        assertEquals(AdminStatus.ACTIVE, user.getStatus());
        assertEquals("System Administrator", user.getFullName());
    }

    @Test
    @DisplayName("Should map AdminUser to AdminUserResponse without exposing password hash")
    void testAdminUserResponseMapping() {
        Instant now = Instant.now();
        AdminUser user = AdminUser.builder()
                .id(2)
                .email("owner@musicradio.com")
                .passwordHash("secret_bcrypt_hash")
                .fullName("Radio Owner")
                .status(AdminStatus.ACTIVE)
                .build();
        user.setCreatedAt(now);
        user.setUpdatedAt(now);

        AdminUserResponse response = AdminUserResponse.fromEntity(user);
        assertNotNull(response);
        assertEquals(2, response.getId());
        assertEquals("owner@musicradio.com", response.getEmail());
        assertEquals("Radio Owner", response.getFullName());
        assertEquals(AdminStatus.ACTIVE, response.getStatus());
        assertEquals(now, response.getCreatedAt());
    }
}

