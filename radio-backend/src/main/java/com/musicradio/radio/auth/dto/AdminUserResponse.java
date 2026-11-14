package com.musicradio.radio.auth.dto;

import com.musicradio.radio.auth.entity.AdminStatus;
import com.musicradio.radio.auth.entity.AdminUser;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

/**
 * Sanitized response DTO representing an administrator profile.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminUserResponse {

    private Integer id;
    private String email;
    private String fullName;
    private AdminStatus status;
    private Instant createdAt;
    private Instant updatedAt;

    public static AdminUserResponse fromEntity(AdminUser adminUser) {
        if (adminUser == null) {
            return null;
        }
        return AdminUserResponse.builder()
                .id(adminUser.getId())
                .email(adminUser.getEmail())
                .fullName(adminUser.getFullName())
                .status(adminUser.getStatus())
                .createdAt(adminUser.getCreatedAt())
                .updatedAt(adminUser.getUpdatedAt())
                .build();
    }
}

