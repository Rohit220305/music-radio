package com.musicradio.radio.auth.config;

import com.musicradio.radio.auth.entity.AdminStatus;
import com.musicradio.radio.auth.entity.AdminUser;
import com.musicradio.radio.auth.repository.AdminUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdminSeederTest {

    @Mock
    private AdminUserRepository adminUserRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AdminSeeder adminSeeder;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(adminSeeder, "defaultEmail", "admin@musicradio.local");
        ReflectionTestUtils.setField(adminSeeder, "defaultPassword", "Admin@123456");
        ReflectionTestUtils.setField(adminSeeder, "defaultName", "System Administrator");
    }

    @Test
    @DisplayName("run when admin repository is empty should seed default administrator")
    void run_WhenNoAdminExists_ShouldSeedDefaultAdmin() {
        when(adminUserRepository.count()).thenReturn(0L);
        when(passwordEncoder.encode("Admin@123456")).thenReturn("bcrypt_hashed_admin_pass");

        adminSeeder.run();

        ArgumentCaptor<AdminUser> captor = ArgumentCaptor.forClass(AdminUser.class);
        verify(adminUserRepository).save(captor.capture());

        AdminUser savedAdmin = captor.getValue();
        assertThat(savedAdmin.getEmail()).isEqualTo("admin@musicradio.local");
        assertThat(savedAdmin.getPasswordHash()).isEqualTo("bcrypt_hashed_admin_pass");
        assertThat(savedAdmin.getFullName()).isEqualTo("System Administrator");
        assertThat(savedAdmin.getStatus()).isEqualTo(AdminStatus.ACTIVE);
    }

    @Test
    @DisplayName("run when admin repository has records should skip seeding")
    void run_WhenAdminsExist_ShouldSkipSeeding() {
        when(adminUserRepository.count()).thenReturn(1L);

        adminSeeder.run();

        verify(adminUserRepository, never()).save(any(AdminUser.class));
    }
}
