package com.musicradio.radio.station.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Validator enforcing:
 * 1. Valid absolute URI with "https" scheme.
 * 2. Host strictly belongs to authentic YouTube domains (youtube.com, www.youtube.com, m.youtube.com, music.youtube.com, youtu.be).
 * 3. Rejection of user-info (e.g. user:pass@youtube.com or youtube.com@attacker.com).
 * 4. Rejection of custom non-standard ports.
 * 5. Presence of a non-empty, valid 'list' query parameter matching playlist ID syntax.
 */
public class YouTubePlaylistUrlValidator implements ConstraintValidator<ValidYouTubePlaylistUrl, String> {

    private static final Set<String> ALLOWED_HOSTS = Set.of(
            "youtube.com",
            "www.youtube.com",
            "m.youtube.com",
            "music.youtube.com",
            "youtu.be"
    );

    // YouTube playlist IDs consist of alphanumeric chars, hyphens, and underscores (e.g. PL..., OLAK5uy_..., UU..., etc.)
    private static final Pattern PLAYLIST_ID_PATTERN = Pattern.compile("^[a-zA-Z0-9_-]{2,100}$");

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isBlank()) {
            // Null or blank values are handled by @NotBlank/@NotNull if required
            return true;
        }

        if (value.contains("\\") || value.contains(" ")) {
            return false;
        }

        URI uri;
        try {
            uri = new URI(value.trim());
        } catch (URISyntaxException e) {
            return false;
        }

        // Must be absolute with https scheme
        if (!"https".equalsIgnoreCase(uri.getScheme())) {
            return false;
        }

        // Reject deceptive userinfo (e.g., https://attacker.com@youtube.com/ or https://user:pass@youtube.com/)
        if (uri.getRawUserInfo() != null) {
            return false;
        }

        // Host validation
        String host = uri.getHost();
        if (host == null) {
            return false;
        }
        host = host.toLowerCase(Locale.ROOT);
        if (!ALLOWED_HOSTS.contains(host)) {
            return false;
        }

        // Reject non-standard ports
        if (uri.getPort() != -1 && uri.getPort() != 443) {
            return false;
        }

        // Query parameter validation for 'list'
        String rawQuery = uri.getRawQuery();
        if (rawQuery == null || rawQuery.isBlank()) {
            return false;
        }

        String playlistId = extractQueryParam(rawQuery, "list");
        if (playlistId == null || playlistId.isBlank()) {
            return false;
        }

        return PLAYLIST_ID_PATTERN.matcher(playlistId).matches();
    }

    private String extractQueryParam(String rawQuery, String paramName) {
        String[] pairs = rawQuery.split("&");
        for (String pair : pairs) {
            int idx = pair.indexOf('=');
            if (idx > 0) {
                String key = pair.substring(0, idx);
                String val = pair.substring(idx + 1);
                if (paramName.equalsIgnoreCase(key)) {
                    return val;
                }
            }
        }
        return null;
    }
}

