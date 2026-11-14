package com.musicradio.radio.auth.security;

import com.musicradio.radio.auth.entity.AdminUser;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * Service component responsible for cryptographic signing, token generation, claim extraction,
 * and token validation using JJWT 0.12.6.
 */
@Slf4j
@Component
public class JwtTokenProvider {

    private final String secret;
    private final long expirationMs;

    public JwtTokenProvider(
            @Value("${jwt.secret:404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970}") String secret,
            @Value("${jwt.expiration-ms:86400000}") long expirationMs) {
        this.secret = secret;
        this.expirationMs = expirationMs;
    }

    private SecretKey getSigningKey() {
        byte[] keyBytes;
        try {
            keyBytes = Decoders.BASE64.decode(secret);
            if (keyBytes.length < 32) {
                keyBytes = secret.getBytes(StandardCharsets.UTF_8);
            }
        } catch (Exception e) {
            keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        }
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Generate a signed JWT token containing email as subject, and id, name, and role claims.
     */
    public String generateToken(AdminUser adminUser) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expirationMs);

        return Jwts.builder()
                .subject(adminUser.getEmail())
                .claim("id", adminUser.getId())
                .claim("name", adminUser.getFullName())
                .claim("role", "ROLE_ADMIN")
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * Extract the email address (subject claim) from a signed token.
     */
    public String extractEmail(String token) {
        return extractClaims(token).getSubject();
    }

    /**
     * Extract the admin Integer ID from custom claims.
     */
    public Integer extractAdminId(String token) {
        Object idObj = extractClaims(token).get("id");
        if (idObj instanceof Number number) {
            return number.intValue();
        }
        return null;
    }

    /**
     * Validate cryptographic signature and expiration of a JWT token string.
     */
    public boolean validateToken(String token) {
        if (token == null || token.isBlank()) {
            return false;
        }
        try {
            Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token.trim());
            return true;
        } catch (JwtException | IllegalArgumentException ex) {
            log.warn("Invalid JWT token: {}", ex.getMessage());
            return false;
        }
    }

    private Claims extractClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token.trim())
                .getPayload();
    }
}
