package com.musicradio.radio.station.controller;

import com.musicradio.radio.common.dto.StatusUpdateRequest;
import com.musicradio.radio.common.exception.ResourceNotFoundException;
import com.musicradio.radio.common.response.ApiResponse;
import com.musicradio.radio.station.dto.CreateStationRequest;
import com.musicradio.radio.station.dto.StationResponse;
import com.musicradio.radio.station.dto.UpdateStationRequest;
import com.musicradio.radio.station.service.StationService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminStationControllerTest {

    @Mock
    private StationService stationService;

    @InjectMocks
    private AdminStationController adminStationController;

    @Test
    @DisplayName("GET /api/v1/admin/stations should return all stations when themeId is omitted")
    void testGetAllStationsAdmin() {
        StationResponse station = StationResponse.builder().id(1).name("Station A").build();
        when(stationService.getAllStationsAdmin(null)).thenReturn(Collections.singletonList(station));

        ResponseEntity<ApiResponse<List<StationResponse>>> response = adminStationController.getAllStations(null);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
        assertEquals(1, response.getBody().getData().size());
        verify(stationService, times(1)).getAllStationsAdmin(null);
    }

    @Test
    @DisplayName("GET /api/v1/admin/stations?themeId=1 should filter stations by theme")
    void testGetStationsByThemeAdmin() {
        StationResponse station = StationResponse.builder().id(1).themeId(1).name("Station A").build();
        when(stationService.getAllStationsAdmin(1)).thenReturn(Collections.singletonList(station));

        ResponseEntity<ApiResponse<List<StationResponse>>> response = adminStationController.getAllStations(1);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
        assertEquals(1, response.getBody().getData().size());
        verify(stationService, times(1)).getAllStationsAdmin(1);
    }

    @Test
    @DisplayName("GET /api/v1/admin/stations/{id} should return 200 OK when found")
    void testGetStationByIdSuccess() {
        StationResponse station = StationResponse.builder().id(1).name("Station A").build();
        when(stationService.getStationByIdAdmin(1)).thenReturn(station);

        ResponseEntity<ApiResponse<StationResponse>> response = adminStationController.getStationById(1);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
        assertEquals("Station A", response.getBody().getData().getName());
        verify(stationService, times(1)).getStationByIdAdmin(1);
    }

    @Test
    @DisplayName("POST /api/v1/admin/stations should return 201 CREATED upon success")
    void testCreateStationSuccess() {
        CreateStationRequest request = CreateStationRequest.builder()
                .themeId(1)
                .name("New Station")
                .slug("new-station")
                .youtubePlaylistUrl("https://www.youtube.com/playlist?list=PL_TEST1")
                .isActive(true)
                .build();

        StationResponse created = StationResponse.builder().id(5).themeId(1).name("New Station").build();
        when(stationService.createStation(any(CreateStationRequest.class))).thenReturn(created);

        ResponseEntity<ApiResponse<StationResponse>> response = adminStationController.createStation(request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
        assertEquals("Station created successfully", response.getBody().getMessage());
        assertEquals(5, response.getBody().getData().getId());
        verify(stationService, times(1)).createStation(request);
    }

    @Test
    @DisplayName("PUT /api/v1/admin/stations/{id} should return 200 OK upon success")
    void testUpdateStationSuccess() {
        UpdateStationRequest request = UpdateStationRequest.builder()
                .themeId(2)
                .name("Updated Station")
                .slug("updated-station")
                .youtubePlaylistUrl("https://www.youtube.com/playlist?list=PL_TEST2")
                .isActive(true)
                .build();

        StationResponse updated = StationResponse.builder().id(1).themeId(2).name("Updated Station").build();
        when(stationService.updateStation(eq(1), any(UpdateStationRequest.class))).thenReturn(updated);

        ResponseEntity<ApiResponse<StationResponse>> response = adminStationController.updateStation(1, request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
        assertEquals("Station updated successfully", response.getBody().getMessage());
        assertEquals("Updated Station", response.getBody().getData().getName());
        verify(stationService, times(1)).updateStation(1, request);
    }

    @Test
    @DisplayName("PATCH /api/v1/admin/stations/{id}/status should return 200 OK")
    void testUpdateStationStatusSuccess() {
        StatusUpdateRequest request = StatusUpdateRequest.builder().isActive(false).build();
        StationResponse updated = StationResponse.builder().id(1).name("Station A").isActive(false).build();
        when(stationService.updateStationStatus(1, request)).thenReturn(updated);

        ResponseEntity<ApiResponse<StationResponse>> response = adminStationController.updateStationStatus(1, request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
        assertEquals("Station status updated successfully", response.getBody().getMessage());
        assertFalse(response.getBody().getData().getIsActive());
        verify(stationService, times(1)).updateStationStatus(1, request);
    }

    @Test
    @DisplayName("DELETE /api/v1/admin/stations/{id} should return 200 OK upon success")
    void testDeleteStationSuccess() {
        doNothing().when(stationService).deleteStation(1);

        ResponseEntity<ApiResponse<Void>> response = adminStationController.deleteStation(1);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
        assertEquals("Station deleted successfully", response.getBody().getMessage());
        verify(stationService, times(1)).deleteStation(1);
    }

    @Test
    @DisplayName("GET /api/v1/admin/stations/{id} should propagate ResourceNotFoundException when not found")
    void testGetStationNotFound() {
        when(stationService.getStationByIdAdmin(999))
                .thenThrow(new ResourceNotFoundException("Station", "id", 999));

        assertThrows(ResourceNotFoundException.class, () -> adminStationController.getStationById(999));
        verify(stationService, times(1)).getStationByIdAdmin(999);
    }
}
