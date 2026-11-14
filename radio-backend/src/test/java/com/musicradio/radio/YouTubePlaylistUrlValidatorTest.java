package com.musicradio.radio;

import com.musicradio.radio.station.validation.YouTubePlaylistUrlValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class YouTubePlaylistUrlValidatorTest {

    private YouTubePlaylistUrlValidator validator;

    @BeforeEach
    void setUp() {
        validator = new YouTubePlaylistUrlValidator();  
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "https://www.youtube.com/playlist?list=PL_EXAMPLE123",
            "https://youtube.com/playlist?list=PL_EXAMPLE123",
            "https://m.youtube.com/playlist?list=PL_EXAMPLE123",
            "https://music.youtube.com/playlist?list=PL_EXAMPLE123",
            "https://www.youtube.com/watch?v=dQw4w9WgXcQ&list=PL_EXAMPLE123",
            "https://youtu.be/dQw4w9WgXcQ?list=PL_EXAMPLE123",
            "https://www.youtube.com/playlist?param1=test&list=OLAK5uy_k123456789&param2=value"
    })
    @DisplayName("Should accept valid HTTPS YouTube playlist URLs")
    void testValidYouTubePlaylistUrls(String url) {
        assertTrue(validator.isValid(url, null), "Expected URL to be valid: " + url);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "http://www.youtube.com/playlist?list=PL_EXAMPLE123",                    // HTTP rejected
            "ftp://www.youtube.com/playlist?list=PL_EXAMPLE123",                     // FTP rejected
            "https://notyoutube.com/playlist?list=PL_EXAMPLE123",                    // Non-YouTube domain
            "https://youtube.com.attacker.com/playlist?list=PL_EXAMPLE123",          // Deceptive subdomain/hostname
            "https://evil-youtube.com/playlist?list=PL_EXAMPLE123",                  // Deceptive domain
            "https://user:password@www.youtube.com/playlist?list=PL_EXAMPLE123",     // Userinfo rejected
            "https://www.youtube.com:8443/playlist?list=PL_EXAMPLE123",              // Custom port rejected
            "https://www.youtube.com/watch?v=dQw4w9WgXcQ",                           // Missing 'list' query parameter
            "https://www.youtube.com/playlist?list=",                                // Empty 'list' parameter
            "https://www.youtube.com/playlist?list=invalid playlist with space",     // Invalid characters in playlist ID
            "not a valid url",                                                       // Malformed syntax
            "https://www.youtube.com\\playlist?list=PL123",                          // Backslash in path
            "javascript:alert(1)"                                                    // Scheme exploit
    })
    @DisplayName("Should reject malformed, non-YouTube, deceptive, or list-missing URLs")
    void testInvalidYouTubePlaylistUrls(String url) {
        assertFalse(validator.isValid(url, null), "Expected URL to be invalid: " + url);
    }
}

