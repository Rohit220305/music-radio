package com.musicradio.radio;

import com.musicradio.radio.station.dto.CreateStationRequest;
import com.musicradio.radio.station.dto.StationResponse;
import com.musicradio.radio.station.entity.Station;
import com.musicradio.radio.theme.entity.Theme;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StationDomainTest {

    @Test
    @DisplayName("Should build Station entity with Integer ID and many-to-one Theme")
    void testStationEntity() {
        Theme theme = Theme.builder().id(5).name("Deep Focus").slug("deep-focus").build();

        Station station = Station.builder()
                .id(50)
                .theme(theme)
                .name("Coding Ambient")
                .slug("coding-ambient")
                .youtubePlaylistUrl("https://www.youtube.com/playlist?list=PL_CODING_123")
                .shuffleEnabled(false)
                .repeatEnabled(true)
                .isActive(true)
                .build();

        assertEquals(50, station.getId());
        assertEquals(5, station.getTheme().getId());
        assertEquals("Coding Ambient", station.getName());
        assertEquals("coding-ambient", station.getSlug());
        assertEquals("https://www.youtube.com/playlist?list=PL_CODING_123", station.getYoutubePlaylistUrl());
        assertFalse(station.getShuffleEnabled());
        assertTrue(station.getRepeatEnabled());
        assertTrue(station.getIsActive());
    }

    @Test
    @DisplayName("Should map Station entity to StationResponse DTO")
    void testStationResponseMapping() {
        Theme theme = Theme.builder().id(3).name("Sunset Drive").build();

        Station station = Station.builder()
                .id(30)
                .theme(theme)
                .name("Chill Synth")
                .slug("chill-synth")
                .youtubePlaylistUrl("https://www.youtube.com/playlist?list=PL_SYNTH")
                .description("Synthesizer melodies for evening drives")
                .shuffleEnabled(true)
                .repeatEnabled(true)
                .isActive(true)
                .build();

        StationResponse response = StationResponse.fromEntity(station);
        assertNotNull(response);
        assertEquals(30, response.getId());
        assertEquals(3, response.getThemeId());
        assertEquals("Sunset Drive", response.getThemeName());
        assertEquals("Chill Synth", response.getName());
        assertEquals("chill-synth", response.getSlug());
        assertTrue(response.getShuffleEnabled());
    }

    @Test
    @DisplayName("Should build CreateStationRequest with expected defaults")
    void testCreateStationRequest() {
        CreateStationRequest request = CreateStationRequest.builder()
                .themeId(3)
                .name("Synth Drive")
                .slug("synth-drive")
                .youtubePlaylistUrl("https://www.youtube.com/playlist?list=PL_SYNTH")
                .build();

        assertEquals(3, request.getThemeId());
        assertEquals("Synth Drive", request.getName());
        assertFalse(request.getShuffleEnabled());
        assertTrue(request.getRepeatEnabled());
        assertTrue(request.getIsActive());
    }
}

