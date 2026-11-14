package com.musicradio.radio.station;

import com.musicradio.radio.common.dto.StatusUpdateRequest;
import com.musicradio.radio.common.exception.DuplicateResourceException;
import com.musicradio.radio.common.exception.ResourceNotFoundException;
import com.musicradio.radio.station.dto.CreateStationRequest;
import com.musicradio.radio.station.dto.StationResponse;
import com.musicradio.radio.station.dto.UpdateStationRequest;
import com.musicradio.radio.station.entity.Station;
import com.musicradio.radio.station.repository.StationRepository;
import com.musicradio.radio.station.service.impl.StationServiceImpl;
import com.musicradio.radio.theme.entity.Theme;
import com.musicradio.radio.theme.repository.ThemeRepository;
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
class StationServiceTest {

    @Mock
    private StationRepository stationRepository;

    @Mock
    private ThemeRepository themeRepository;

    @InjectMocks
    private StationServiceImpl stationService;

    private Theme themeA;
    private Theme themeB;
    private Station station;

    @BeforeEach
    void setUp() {
        themeA = Theme.builder()
                .id(1)
                .name("Rainy Night")
                .slug("rainy-night")
                .isActive(true)
                .stations(new ArrayList<>())
                .build();

        themeB = Theme.builder()
                .id(2)
                .name("Coffee Shop")
                .slug("coffee-shop")
                .isActive(true)
                .stations(new ArrayList<>())
                .build();

        station = Station.builder()
                .id(10)
                .theme(themeA)
                .name("Lo-Fi Beats")
                .slug("lofi-beats")
                .youtubePlaylistUrl("https://www.youtube.com/playlist?list=PL_TEST_123")
                .description("Cozy beats")
                .shuffleEnabled(false)
                .repeatEnabled(true)
                .isActive(true)
                .build();

        themeA.getStations().add(station);
    }

    @Test
    @DisplayName("getPublicStationById should return station when both station and theme are active")
    void testGetPublicStationByIdSuccess() {
        when(stationRepository.findPubliclyAvailableById(10)).thenReturn(Optional.of(station));

        StationResponse response = stationService.getPublicStationById(10);

        assertNotNull(response);
        assertEquals(10, response.getId());
        assertEquals("lofi-beats", response.getSlug());
        assertEquals(1, response.getThemeId());
        assertEquals("Rainy Night", response.getThemeName());
    }

    @Test
    @DisplayName("getPublicStationById should throw ResourceNotFoundException when dual-active check fails")
    void testGetPublicStationByIdNotFound() {
        when(stationRepository.findPubliclyAvailableById(99)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> stationService.getPublicStationById(99));
    }

    @Test
    @DisplayName("getAllStationsAdmin without themeId should return all stations ordered by id")
    void testGetAllStationsAdminWithoutFilter() {
        when(stationRepository.findAllByOrderByIdAsc()).thenReturn(Collections.singletonList(station));

        List<StationResponse> results = stationService.getAllStationsAdmin(null);

        assertEquals(1, results.size());
        assertEquals("Lo-Fi Beats", results.get(0).getName());
    }

    @Test
    @DisplayName("getAllStationsAdmin with valid themeId should filter stations by theme")
    void testGetAllStationsAdminWithThemeId() {
        when(themeRepository.existsById(1)).thenReturn(true);
        when(stationRepository.findByThemeIdOrderByIdAsc(1)).thenReturn(Collections.singletonList(station));

        List<StationResponse> results = stationService.getAllStationsAdmin(1);

        assertEquals(1, results.size());
        assertEquals(1, results.get(0).getThemeId());
    }

    @Test
    @DisplayName("getAllStationsAdmin with invalid themeId should throw ResourceNotFoundException")
    void testGetAllStationsAdminWithInvalidThemeId() {
        when(themeRepository.existsById(999)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class,
                () -> stationService.getAllStationsAdmin(999));
    }

    @Test
    @DisplayName("getStationByIdAdmin should return station by id")
    void testGetStationByIdAdminSuccess() {
        when(stationRepository.findById(10)).thenReturn(Optional.of(station));

        StationResponse response = stationService.getStationByIdAdmin(10);

        assertNotNull(response);
        assertEquals(10, response.getId());
    }

    @Test
    @DisplayName("getStationByIdAdmin should throw ResourceNotFoundException when ID does not exist")
    void testGetStationByIdAdminNotFound() {
        when(stationRepository.findById(999)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> stationService.getStationByIdAdmin(999));
    }

    @Test
    @DisplayName("createStation should save station and link to theme when inputs are valid")
    void testCreateStationSuccess() {
        CreateStationRequest request = CreateStationRequest.builder()
                .themeId(1)
                .name("Jazz Rain")
                .slug("jazz-rain")
                .youtubePlaylistUrl("https://www.youtube.com/playlist?list=PL_JAZZ")
                .description("Jazz ambience")
                .shuffleEnabled(true)
                .repeatEnabled(true)
                .isActive(true)
                .build();

        when(themeRepository.findById(1)).thenReturn(Optional.of(themeA));
        when(stationRepository.existsBySlug("jazz-rain")).thenReturn(false);
        when(stationRepository.save(any(Station.class))).thenAnswer(invocation -> {
            Station s = invocation.getArgument(0);
            s.setId(25);
            return s;
        });

        StationResponse response = stationService.createStation(request);

        assertNotNull(response);
        assertEquals(25, response.getId());
        assertEquals("Jazz Rain", response.getName());
        assertEquals("jazz-rain", response.getSlug());
        assertEquals(1, response.getThemeId());
    }

    @Test
    @DisplayName("createStation should throw ResourceNotFoundException when themeId does not exist")
    void testCreateStationThemeNotFound() {
        CreateStationRequest request = CreateStationRequest.builder()
                .themeId(999)
                .name("Jazz Rain")
                .slug("jazz-rain")
                .youtubePlaylistUrl("https://www.youtube.com/playlist?list=PL_JAZZ")
                .build();

        when(themeRepository.findById(999)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> stationService.createStation(request));
        verify(stationRepository, never()).save(any());
    }

    @Test
    @DisplayName("createStation should throw DuplicateResourceException when slug already exists")
    void testCreateStationSlugConflict() {
        CreateStationRequest request = CreateStationRequest.builder()
                .themeId(1)
                .name("Lo-Fi Beats")
                .slug("lofi-beats")
                .youtubePlaylistUrl("https://www.youtube.com/playlist?list=PL_TEST_123")
                .build();

        when(themeRepository.findById(1)).thenReturn(Optional.of(themeA));
        when(stationRepository.existsBySlug("lofi-beats")).thenReturn(true);

        assertThrows(DuplicateResourceException.class,
                () -> stationService.createStation(request));
        verify(stationRepository, never()).save(any());
    }

    @Test
    @DisplayName("updateStation should update fields within same theme when valid")
    void testUpdateStationSameThemeSuccess() {
        UpdateStationRequest request = UpdateStationRequest.builder()
                .themeId(1)
                .name("Lo-Fi Beats Remastered")
                .slug("lofi-beats-remastered")
                .youtubePlaylistUrl("https://www.youtube.com/playlist?list=PL_UPDATED")
                .description("New description")
                .shuffleEnabled(true)
                .repeatEnabled(false)
                .isActive(true)
                .build();

        when(stationRepository.findById(10)).thenReturn(Optional.of(station));
        when(stationRepository.existsBySlugAndIdNot("lofi-beats-remastered", 10)).thenReturn(false);
        when(stationRepository.save(any(Station.class))).thenReturn(station);

        StationResponse response = stationService.updateStation(10, request);

        assertNotNull(response);
        assertEquals("Lo-Fi Beats Remastered", station.getName());
        assertEquals("lofi-beats-remastered", station.getSlug());
        assertTrue(station.getShuffleEnabled());
        assertFalse(station.getRepeatEnabled());
    }

    @Test
    @DisplayName("updateStation should throw DuplicateResourceException when new slug collides with another station")
    void testUpdateStationSlugCollision() {
        UpdateStationRequest request = UpdateStationRequest.builder()
                .themeId(1)
                .name("Other Station")
                .slug("other-station")
                .youtubePlaylistUrl("https://www.youtube.com/playlist?list=PL_TEST")
                .shuffleEnabled(false)
                .repeatEnabled(true)
                .isActive(true)
                .build();

        when(stationRepository.findById(10)).thenReturn(Optional.of(station));
        when(stationRepository.existsBySlugAndIdNot("other-station", 10)).thenReturn(true);

        assertThrows(DuplicateResourceException.class,
                () -> stationService.updateStation(10, request));
        verify(stationRepository, never()).save(any());
    }

    @Test
    @DisplayName("updateStation should consistently reassign station between themes updating both sides of relationship")
    void testUpdateStationThemeReassignmentSuccess() {
        UpdateStationRequest request = UpdateStationRequest.builder()
                .themeId(2) // Reassign from Theme 1 (themeA) to Theme 2 (themeB)
                .name("Lo-Fi Beats")
                .slug("lofi-beats")
                .youtubePlaylistUrl("https://www.youtube.com/playlist?list=PL_TEST_123")
                .shuffleEnabled(false)
                .repeatEnabled(true)
                .isActive(true)
                .build();

        when(stationRepository.findById(10)).thenReturn(Optional.of(station));
        when(stationRepository.existsBySlugAndIdNot("lofi-beats", 10)).thenReturn(false);
        when(themeRepository.findById(2)).thenReturn(Optional.of(themeB));
        when(stationRepository.save(any(Station.class))).thenReturn(station);

        assertEquals(1, themeA.getStations().size());
        assertEquals(0, themeB.getStations().size());

        StationResponse response = stationService.updateStation(10, request);

        assertNotNull(response);
        assertEquals(themeB, station.getTheme());
        assertFalse(themeA.getStations().contains(station), "Old theme must remove the station");
        assertTrue(themeB.getStations().contains(station), "New theme must contain the station");
    }

    @Test
    @DisplayName("updateStation should throw ResourceNotFoundException when reassigned theme does not exist")
    void testUpdateStationThemeReassignmentNotFound() {
        UpdateStationRequest request = UpdateStationRequest.builder()
                .themeId(999)
                .name("Lo-Fi Beats")
                .slug("lofi-beats")
                .youtubePlaylistUrl("https://www.youtube.com/playlist?list=PL_TEST_123")
                .shuffleEnabled(false)
                .repeatEnabled(true)
                .isActive(true)
                .build();

        when(stationRepository.findById(10)).thenReturn(Optional.of(station));
        when(stationRepository.existsBySlugAndIdNot("lofi-beats", 10)).thenReturn(false);
        when(themeRepository.findById(999)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> stationService.updateStation(10, request));
        verify(stationRepository, never()).save(any());
    }

    @Test
    @DisplayName("updateStationStatus should toggle station active state")
    void testUpdateStationStatus() {
        StatusUpdateRequest request = StatusUpdateRequest.builder().isActive(false).build();
        when(stationRepository.findById(10)).thenReturn(Optional.of(station));
        when(stationRepository.save(any(Station.class))).thenReturn(station);

        StationResponse response = stationService.updateStationStatus(10, request);

        assertNotNull(response);
        assertFalse(station.getIsActive());
    }

    @Test
    @DisplayName("deleteStation should disassociate station from parent theme and delete entity")
    void testDeleteStationSuccess() {
        when(stationRepository.findById(10)).thenReturn(Optional.of(station));

        assertEquals(1, themeA.getStations().size());

        stationService.deleteStation(10);

        assertFalse(themeA.getStations().contains(station));
        verify(stationRepository, times(1)).delete(station);
    }

    @Test
    @DisplayName("deleteStation should throw ResourceNotFoundException when ID does not exist")
    void testDeleteStationNotFound() {
        when(stationRepository.findById(999)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> stationService.deleteStation(999));
        verify(stationRepository, never()).delete(any());
    }
}

