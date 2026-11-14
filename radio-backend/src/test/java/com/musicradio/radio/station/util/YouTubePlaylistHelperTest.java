package com.musicradio.radio.station.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class YouTubePlaylistHelperTest {

    @ParameterizedTest
    @DisplayName("Should correctly extract playlist ID from valid playlist URLs")
    @CsvSource({
            "https://www.youtube.com/playlist?list=PL_EXAMPLE123, PL_EXAMPLE123",
            "https://youtube.com/playlist?list=PLw-x083-2848, PLw-x083-2848",
            "https://music.youtube.com/playlist?list=PL_MUSIC_999, PL_MUSIC_999",
            "https://m.youtube.com/playlist?list=PL_MOBILE_123, PL_MOBILE_123",
            "https://youtu.be/watch?v=dQw4w9WgXcQ&list=PL_SHORT_456, PL_SHORT_456",
            "https://www.youtube.com/watch?v=abc&list=PL_WITH_INDEX&index=4&t=10s, PL_WITH_INDEX",
            "https://www.youtube.com/watch?feature=shared&list=PL_FEATURE_FIRST, PL_FEATURE_FIRST"
    })
    void testExtractPlaylistIdValid(String url, String expectedPlaylistId) {
        Optional<String> playlistId = YouTubePlaylistHelper.extractPlaylistId(url);
        assertTrue(playlistId.isPresent());
        assertEquals(expectedPlaylistId.trim(), playlistId.get());
    }

    @ParameterizedTest
    @DisplayName("Should return Optional.empty() when playlist ID is missing, malformed, or invalid")
    @NullAndEmptySource
    @ValueSource(strings = {
            "   ",
            "https://www.youtube.com/watch?v=dQw4w9WgXcQ",
            "https://www.youtube.com/playlist",
            "https://www.youtube.com/playlist?list=",
            "https://www.youtube.com/playlist?list=a",
            "https://www.youtube.com/playlist?list=a!@#$",
            "not a url",
            "https://example.com/notyoutube"
    })
    void testExtractPlaylistIdInvalid(String url) {
        Optional<String> playlistId = YouTubePlaylistHelper.extractPlaylistId(url);
        assertTrue(playlistId.isEmpty());
    }
}

