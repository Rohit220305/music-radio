package com.musicradio.radio.theme;

import com.musicradio.radio.common.dto.StatusUpdateRequest;
import com.musicradio.radio.common.exception.BadRequestException;
import com.musicradio.radio.common.exception.DuplicateResourceException;
import com.musicradio.radio.common.exception.ResourceNotFoundException;
import com.musicradio.radio.station.entity.Station;
import com.musicradio.radio.station.repository.StationRepository;
import com.musicradio.radio.theme.dto.CreateThemeRequest;
import com.musicradio.radio.theme.dto.ThemeResponse;
import com.musicradio.radio.theme.dto.UpdateThemeRequest;
import com.musicradio.radio.theme.entity.Theme;
import com.musicradio.radio.theme.repository.ThemeRepository;
import com.musicradio.radio.theme.service.impl.ThemeServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ThemeServiceTest {

    @Mock
    private ThemeRepository themeRepository;

    @Mock
    private StationRepository stationRepository;

    @InjectMocks
    private ThemeServiceImpl themeService;

    private Theme activeTheme;
    private Station activeStation;
    private Station inactiveStation;

    @BeforeEach
    void setUp() {
        activeTheme = Theme.builder()
                .id(1)
                .name("Rainy Night")
                .slug("rainy-night")
                .description("Cozy ambiance")
                .backgroundImage("https://example.com/bg.jpg")
                .thumbnailImage("https://example.com/thumb.jpg")
                .sortOrder(1)
                .isActive(true)
                .stations(new ArrayList<>())
                .build();

        activeStation = Station.builder()
                .id(10)
                .theme(activeTheme)
                .name("Lo-Fi Rain")
                .slug("lofi-rain")
                .youtubePlaylistUrl("https://www.youtube.com/playlist?list=PL_RAIN_1")
                .isActive(true)
                .build();

        inactiveStation = Station.builder()
                .id(11)
                .theme(activeTheme)
                .name("Offline Rain")
                .slug("offline-rain")
                .youtubePlaylistUrl("https://www.youtube.com/playlist?list=PL_RAIN_2")
                .isActive(false)
                .build();

        activeTheme.getStations().add(activeStation);
        activeTheme.getStations().add(inactiveStation);
    }

    @Test
    @DisplayName("getPublicThemes should return active themes and filter out inactive stations in memory")
    void testGetPublicThemesFiltersInactiveStations() {
        when(themeRepository.findByIsActiveTrueOrderBySortOrderAscIdAsc())
                .thenReturn(Collections.singletonList(activeTheme));

        List<ThemeResponse> result = themeService.getPublicThemes();

        assertNotNull(result);
        assertEquals(1, result.size());
        ThemeResponse themeDto = result.get(0);
        assertEquals(1, themeDto.getId());
        assertEquals("rainy-night", themeDto.getSlug());
        // Crucial: Only the 1 active station should be mapped for public view
        assertEquals(1, themeDto.getStations().size());
        assertEquals("lofi-rain", themeDto.getStations().get(0).getSlug());
    }

    @Test
    @DisplayName("getPublicThemeBySlug should return active theme with active stations only")
    void testGetPublicThemeBySlugSuccess() {
        when(themeRepository.findBySlugAndIsActiveTrue("rainy-night"))
                .thenReturn(Optional.of(activeTheme));

        ThemeResponse response = themeService.getPublicThemeBySlug("rainy-night");

        assertNotNull(response);
        assertEquals("rainy-night", response.getSlug());
        assertEquals(1, response.getStations().size());
        assertEquals(10, response.getStations().get(0).getId());
    }

    @Test
    @DisplayName("getPublicThemeBySlug should throw ResourceNotFoundException when theme is inactive or not found")
    void testGetPublicThemeBySlugNotFound() {
        when(themeRepository.findBySlugAndIsActiveTrue("inactive-theme"))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> themeService.getPublicThemeBySlug("inactive-theme"));
    }

    @Test
    @DisplayName("getAllThemesAdmin should return all themes and all stations regardless of active state")
    void testGetAllThemesAdmin() {
        Theme inactiveTheme = Theme.builder()
                .id(2)
                .name("Archived Theme")
                .slug("archived-theme")
                .isActive(false)
                .stations(new ArrayList<>())
                .build();

        when(themeRepository.findAllWithStations())
                .thenReturn(Arrays.asList(activeTheme, inactiveTheme));

        List<ThemeResponse> result = themeService.getAllThemesAdmin();

        assertEquals(2, result.size());
        // Admin view must preserve both active and inactive stations
        assertEquals(2, result.get(0).getStations().size());
    }

    @Test
    @DisplayName("getThemeByIdAdmin should return theme by ID with all stations")
    void testGetThemeByIdAdminSuccess() {
        when(themeRepository.findById(1)).thenReturn(Optional.of(activeTheme));

        ThemeResponse response = themeService.getThemeByIdAdmin(1);

        assertNotNull(response);
        assertEquals(1, response.getId());
        assertEquals(2, response.getStations().size());
    }

    @Test
    @DisplayName("getThemeByIdAdmin should throw ResourceNotFoundException when ID does not exist")
    void testGetThemeByIdAdminNotFound() {
        when(themeRepository.findById(999)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> themeService.getThemeByIdAdmin(999));
    }

    @Test
    @DisplayName("createTheme should save new theme when slug is unique")
    void testCreateThemeSuccess() {
        CreateThemeRequest request = CreateThemeRequest.builder()
                .name("Coffee Shop")
                .slug("coffee-shop")
                .description("Cozy cafe")
                .sortOrder(2)
                .isActive(true)
                .build();

        when(themeRepository.existsBySlug("coffee-shop")).thenReturn(false);
        when(themeRepository.save(any(Theme.class))).thenAnswer(invocation -> {
            Theme saved = invocation.getArgument(0);
            saved.setId(20);
            return saved;
        });

        ThemeResponse response = themeService.createTheme(request);

        assertNotNull(response);
        assertEquals(20, response.getId());
        assertEquals("Coffee Shop", response.getName());
        assertEquals("coffee-shop", response.getSlug());
    }

    @Test
    @DisplayName("createTheme should throw DuplicateResourceException when slug already exists")
    void testCreateThemeDuplicateSlug() {
        CreateThemeRequest request = CreateThemeRequest.builder()
                .name("Rainy Night")
                .slug("rainy-night")
                .build();

        when(themeRepository.existsBySlug("rainy-night")).thenReturn(true);

        assertThrows(DuplicateResourceException.class,
                () -> themeService.createTheme(request));
        verify(themeRepository, never()).save(any());
    }

    @Test
    @DisplayName("updateTheme should update existing theme when slug does not collide")
    void testUpdateThemeSuccess() {
        UpdateThemeRequest request = UpdateThemeRequest.builder()
                .name("Rainy Night Deluxe")
                .slug("rainy-night-deluxe")
                .description("Updated ambiance")
                .sortOrder(3)
                .isActive(true)
                .build();

        when(themeRepository.findById(1)).thenReturn(Optional.of(activeTheme));
        when(themeRepository.existsBySlugAndIdNot("rainy-night-deluxe", 1)).thenReturn(false);
        when(themeRepository.save(any(Theme.class))).thenReturn(activeTheme);

        ThemeResponse response = themeService.updateTheme(1, request);

        assertNotNull(response);
        assertEquals("Rainy Night Deluxe", activeTheme.getName());
        assertEquals("rainy-night-deluxe", activeTheme.getSlug());
        assertEquals(3, activeTheme.getSortOrder());
    }

    @Test
    @DisplayName("updateTheme should throw DuplicateResourceException when slug collides with another theme")
    void testUpdateThemeSlugCollision() {
        UpdateThemeRequest request = UpdateThemeRequest.builder()
                .name("Coffee Shop")
                .slug("coffee-shop")
                .sortOrder(1)
                .isActive(true)
                .build();

        when(themeRepository.findById(1)).thenReturn(Optional.of(activeTheme));
        when(themeRepository.existsBySlugAndIdNot("coffee-shop", 1)).thenReturn(true);

        assertThrows(DuplicateResourceException.class,
                () -> themeService.updateTheme(1, request));
        verify(themeRepository, never()).save(any());
    }

    @Test
    @DisplayName("updateThemeStatus should toggle active state")
    void testUpdateThemeStatus() {
        StatusUpdateRequest request = StatusUpdateRequest.builder().isActive(false).build();
        when(themeRepository.findById(1)).thenReturn(Optional.of(activeTheme));
        when(themeRepository.save(any(Theme.class))).thenReturn(activeTheme);

        ThemeResponse response = themeService.updateThemeStatus(1, request);

        assertNotNull(response);
        assertFalse(activeTheme.getIsActive());
    }

    @Test
    @DisplayName("deleteTheme should throw BadRequestException when theme contains attached stations")
    void testDeleteThemeWithStationsThrowsBadRequest() {
        when(themeRepository.findById(1)).thenReturn(Optional.of(activeTheme));
        when(stationRepository.existsByThemeId(1)).thenReturn(true);

        BadRequestException ex = assertThrows(BadRequestException.class,
                () -> themeService.deleteTheme(1));
        assertTrue(ex.getMessage().contains("contains associated stations"));
        verify(themeRepository, never()).delete(any());
    }

    @Test
    @DisplayName("deleteTheme should delete theme cleanly when zero stations exist")
    void testDeleteThemeSuccess() {
        Theme emptyTheme = Theme.builder().id(5).name("Empty").slug("empty").build();
        when(themeRepository.findById(5)).thenReturn(Optional.of(emptyTheme));
        when(stationRepository.existsByThemeId(5)).thenReturn(false);

        themeService.deleteTheme(5);

        verify(themeRepository, times(1)).delete(emptyTheme);
    }
}

