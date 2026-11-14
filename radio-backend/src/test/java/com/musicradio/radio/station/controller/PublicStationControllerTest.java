package com.musicradio.radio.station.controller;

import com.musicradio.radio.common.exception.ResourceNotFoundException;
import com.musicradio.radio.common.response.ApiResponse;
import com.musicradio.radio.station.dto.StationResponse;
import com.musicradio.radio.station.service.StationService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PublicStationControllerTest {

    @Mock
    private StationService stationService;

    @InjectMocks
    private PublicStationController publicStationController;

    @Test
    @DisplayName("GET /api/v1/public/stations/{id} should return 200 OK when station is active and theme is active")
    void testGetPublicStationByIdSuccess() {
        StationResponse station = StationResponse.builder()
                .id(10)
                .themeId(1)
                .themeName("Rainy Night")
                .name("Lo-Fi Beats")
                .slug("lofi-beats")
                .youtubePlaylistUrl("https://www.youtube.com/playlist?list=PL_EXAMPLE1")
                .isActive(true)
                .build();

        when(stationService.getPublicStationById(10)).thenReturn(station);

        ResponseEntity<ApiResponse<StationResponse>> response = publicStationController.getPublicStationById(10);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
        assertEquals("Station retrieved successfully", response.getBody().getMessage());
        assertEquals(10, response.getBody().getData().getId());
        assertEquals("Lo-Fi Beats", response.getBody().getData().getName());
        verify(stationService, times(1)).getPublicStationById(10);
    }

    @Test
    @DisplayName("GET /api/v1/public/stations/{id} should propagate ResourceNotFoundException when station or theme is inactive or missing")
    void testGetPublicStationByIdNotFound() {
        when(stationService.getPublicStationById(999))
                .thenThrow(new ResourceNotFoundException("Station", "id", 999));

        assertThrows(ResourceNotFoundException.class, () -> publicStationController.getPublicStationById(999));
        verify(stationService, times(1)).getPublicStationById(999);
    }
}
