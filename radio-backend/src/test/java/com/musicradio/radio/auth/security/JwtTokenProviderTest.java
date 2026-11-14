package com.musicradio.radio.auth.security;

import com.musicradio.radio.auth.entity.AdminStatus;
import com.musicradio.radio.auth.entity.AdminUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class JwtTokenProviderTest {

    private static final String TEST_SECRET = "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970";
    private static final long TEST_EXPIRATION_MS = 3600000; // 1 hour

    private JwtTokenProvider tokenProvider;
    private AdminUser testAdmin;

    @BeforeEach
    void setUp() {
        tokenProvider = new JwtTokenProvider(TEST_SECRET, TEST_EXPIRATION_MS);

        testAdmin = AdminUser.builder()
                .id(1)
                .email("admin@musicradio.local")
                .fullName("System Administrator")
                .passwordHash("hashed_password")
                .status(AdminStatus.ACTIVE)
                .build();
    }

    @Test
    @DisplayName("generateToken should generate a valid, non-blank signed token")
    void generateToken_ShouldGenerateValidSignedToken() {
        String token = tokenProvider.generateToken(testAdmin);

        assertThat(token).isNotBlank();
        assertThat(tokenProvider.validateToken(token)).isTrue();
    }

    @Test
    @DisplayName("extractEmail should return correct email from valid token")
    void extractEmail_ShouldReturnCorrectEmail() {
        String token = tokenProvider.generateToken(testAdmin);

        String email = tokenProvider.extractEmail(token);
        assertThat(email).isEqualTo("admin@musicradio.local");
    }

    @Test
    @DisplayName("extractAdminId should return correct Integer ID from valid token")
    void extractAdminId_ShouldReturnCorrectId() {
        String token = tokenProvider.generateToken(testAdmin);

        Integer id = tokenProvider.extractAdminId(token);
        assertThat(id).isEqualTo(1);
    }

    @Test
    @DisplayName("validateToken should return false for expired token")
    void validateToken_WithExpiredToken_ShouldReturnFalse() {
        // Expiration of -1000ms creates an already-expired token
        JwtTokenProvider expiredProvider = new JwtTokenProvider(TEST_SECRET, -1000L);
        String expiredToken = expiredProvider.generateToken(testAdmin);

        assertThat(tokenProvider.validateToken(expiredToken)).isFalse();
    }

    @Test
    @DisplayName("validateToken should return false for tampered signature")
    void validateToken_WithTamperedToken_ShouldReturnFalse() {
        String token = tokenProvider.generateToken(testAdmin);
        String tamperedToken = token.substring(0, token.length() - 5) + "abcde";

        assertThat(tokenProvider.validateToken(tamperedToken)).isFalse();
    }

    @Test
    @DisplayName("validateToken should return false for malformed token string")
    void validateToken_WithMalformedString_ShouldReturnFalse() {
        assertThat(tokenProvider.validateToken("not.a.valid.jwt")).isFalse();
    }

    @Test
    @DisplayName("validateToken should return false for null or blank string")
    void validateToken_WithNullOrBlank_ShouldReturnFalse() {
        assertThat(tokenProvider.validateToken(null)).isFalse();
        assertThat(tokenProvider.validateToken("")).isFalse();
        assertThat(tokenProvider.validateToken("   ")).isFalse();
    }
}
