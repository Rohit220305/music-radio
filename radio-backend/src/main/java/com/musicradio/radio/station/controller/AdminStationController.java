package com.musicradio.radio.station.controller;

import com.musicradio.radio.common.dto.StatusUpdateRequest;
import com.musicradio.radio.common.response.ApiResponse;
import com.musicradio.radio.station.dto.CreateStationRequest;
import com.musicradio.radio.station.dto.StationResponse;
import com.musicradio.radio.station.dto.UpdateStationRequest;
import com.musicradio.radio.station.service.StationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Administrative REST controller for CRUD operations on Stations.
 */
@RestController
@RequestMapping("/api/v1/admin/stations")
@RequiredArgsConstructor
public class AdminStationController {

    private final StationService stationService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<StationResponse>>> getAllStations(
            @RequestParam(required = false) Integer themeId) {
        List<StationResponse> stations = stationService.getAllStationsAdmin(themeId);
        return ResponseEntity.ok(ApiResponse.success(stations, "Stations retrieved successfully"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<StationResponse>> getStationById(@PathVariable Integer id) {
        StationResponse station = stationService.getStationByIdAdmin(id);
        return ResponseEntity.ok(ApiResponse.success(station, "Station retrieved successfully"));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<StationResponse>> createStation(
            @Valid @RequestBody CreateStationRequest request) {
        StationResponse created = stationService.createStation(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(created, "Station created successfully"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<StationResponse>> updateStation(
            @PathVariable Integer id,
            @Valid @RequestBody UpdateStationRequest request) {
        StationResponse updated = stationService.updateStation(id, request);
        return ResponseEntity.ok(ApiResponse.success(updated, "Station updated successfully"));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<StationResponse>> updateStationStatus(
            @PathVariable Integer id,
            @Valid @RequestBody StatusUpdateRequest request) {
        StationResponse updated = stationService.updateStationStatus(id, request);
        return ResponseEntity.ok(ApiResponse.success(updated, "Station status updated successfully"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteStation(@PathVariable Integer id) {
        stationService.deleteStation(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Station deleted successfully"));
    }
}
