package com.musicradio.radio.auth.config;

import com.musicradio.radio.auth.entity.AdminStatus;
import com.musicradio.radio.auth.entity.AdminUser;
import com.musicradio.radio.auth.repository.AdminUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Startup seeder that initializes a default administrative user account
 * if and only if the admin_users table contains zero records.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AdminSeeder implements CommandLineRunner {

    private final AdminUserRepository adminUserRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.admin.default-email:admin@musicradio.local}")
    private String defaultEmail;

    @Value("${app.admin.default-password:Admin@123456}")
    private String defaultPassword;

    @Value("${app.admin.default-name:System Administrator}")
    private String defaultName;

    @Override
    public void run(String... args) {
        long adminCount = adminUserRepository.count();
        if (adminCount == 0) {
            log.info("No administrator accounts detected. Initializing default admin user...");
            AdminUser defaultAdmin = AdminUser.builder()
                    .email(defaultEmail.toLowerCase().trim())
                    .passwordHash(passwordEncoder.encode(defaultPassword))
                    .fullName(defaultName)
                    .status(AdminStatus.ACTIVE)
                    .build();

            adminUserRepository.save(defaultAdmin);
            log.info("Default administrator seeded successfully with email: {}", defaultEmail);
        } else {
            log.info("Administrator accounts already exist (count: {}). Skipping default admin seeder.", adminCount);
        }
    }
}
