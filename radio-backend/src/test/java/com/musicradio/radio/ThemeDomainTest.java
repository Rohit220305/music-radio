package com.musicradio.radio;

import com.musicradio.radio.station.entity.Station;
import com.musicradio.radio.theme.dto.CreateThemeRequest;
import com.musicradio.radio.theme.dto.ThemeResponse;
import com.musicradio.radio.theme.dto.ThemeSummaryResponse;
import com.musicradio.radio.theme.entity.Theme;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ThemeDomainTest {

    @Test
    @DisplayName("Should create Theme entity with Integer ID and One-to-Many Stations association")
    void testThemeEntityAndStationRelationship() {
        Theme theme = Theme.builder()
                .id(1)
                .name("Rainy Night")
                .slug("rainy-night")
                .description("Cozy rain sounds with lofi music")
                .backgroundImage("/themes/rainy-night.webp")
                .thumbnailImage("/themes/rainy-night-thumb.webp")
                .isActive(true)
                .sortOrder(1)
                .build();

        Station station1 = Station.builder()
                .id(1)
                .name("Rainy Lo-fi Station")
                .slug("rainy-lofi")
                .youtubePlaylistUrl("https://www.youtube.com/playlist?list=PL_EXAMPLE1")
                .isActive(true)
                .build();

        Station station2 = Station.builder()
                .id(2)
                .name("Rainy Jazz Station")
                .slug("rainy-jazz")
                .youtubePlaylistUrl("https://www.youtube.com/playlist?list=PL_EXAMPLE2")
                .isActive(true)
                .build();

        theme.addStation(station1);
        theme.addStation(station2);

        assertEquals(2, theme.getStations().size());
        assertEquals(theme, station1.getTheme());
        assertEquals(theme, station2.getTheme());

        theme.removeStation(station1);
        assertEquals(1, theme.getStations().size());
        assertNotNull(station1.getTheme());
        assertEquals(theme, station1.getTheme());
    }

    @Test
    @DisplayName("Should map Theme entity to ThemeResponse and ThemeSummaryResponse DTOs")
    void testThemeDtoMapping() {
        Theme theme = Theme.builder()
                .id(10)
                .name("Coffee Shop")
                .slug("coffee-shop")
                .description("Warm acoustic coffee shop ambience")
                .thumbnailImage("/themes/coffee-thumb.webp")
                .isActive(true)
                .sortOrder(2)
                .build();

        Station station = Station.builder()
                .id(101)
                .name("Acoustic Coffee")
                .slug("acoustic-coffee")
                .youtubePlaylistUrl("https://www.youtube.com/playlist?list=PL_COFFEE")
                .build();

        theme.addStation(station);

        ThemeResponse response = ThemeResponse.fromEntity(theme);
        assertNotNull(response);
        assertEquals(10, response.getId());
        assertEquals("Coffee Shop", response.getName());
        assertEquals(1, response.getStations().size());
        assertEquals("Acoustic Coffee", response.getStations().get(0).getName());

        ThemeSummaryResponse summary = ThemeSummaryResponse.fromEntity(theme);
        assertNotNull(summary);
        assertEquals(10, summary.getId());
        assertEquals("Coffee Shop", summary.getName());
        assertEquals("/themes/coffee-thumb.webp", summary.getThumbnailImage());
    }

    @Test
    @DisplayName("Should build CreateThemeRequest with default values")
    void testCreateThemeRequest() {
        CreateThemeRequest request = CreateThemeRequest.builder()
                .name("Neon Nights")
                .slug("neon-nights")
                .build();

        assertEquals("Neon Nights", request.getName());
        assertEquals("neon-nights", request.getSlug());
        assertTrue(request.getIsActive());
        assertEquals(0, request.getSortOrder());
    }
}

