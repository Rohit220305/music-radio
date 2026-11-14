package com.musicradio.radio.station.util;

import java.net.URI;
import java.util.Optional;
import java.util.regex.Pattern;

/**
 * Utility class for extracting YouTube playlist IDs from playlist URLs.
 * Retains complete playlist URLs in the database and DTO contracts.
 */
public final class YouTubePlaylistHelper {

    private static final Pattern PLAYLIST_ID_PATTERN = Pattern.compile("^[a-zA-Z0-9_-]{2,100}$");

    private YouTubePlaylistHelper() {
        // Prevent instantiation of utility class
    }

    /**
     * Extracts the playlist ID parameter ("list") from a YouTube playlist URL.
     *
     * @param youtubePlaylistUrl the full YouTube URL
     * @return Optional containing the playlist ID, or empty if absent or invalid
     */
    public static Optional<String> extractPlaylistId(String youtubePlaylistUrl) {
        if (youtubePlaylistUrl == null || youtubePlaylistUrl.isBlank()) {
            return Optional.empty();
        }

        try {
            URI uri = URI.create(youtubePlaylistUrl.trim());
            String query = uri.getQuery();
            if (query == null || query.isBlank()) {
                return Optional.empty();
            }

            for (String param : query.split("&")) {
                String[] pair = param.split("=", 2);
                if (pair.length == 2 && "list".equalsIgnoreCase(pair[0])) {
                    String listValue = pair[1].trim();
                    if (PLAYLIST_ID_PATTERN.matcher(listValue).matches()) {
                        return Optional.of(listValue);
                    }
                }
            }
        } catch (Exception e) {
            // Malformed URL or non-standard URI syntax
        }

        return Optional.empty();
    }
}

