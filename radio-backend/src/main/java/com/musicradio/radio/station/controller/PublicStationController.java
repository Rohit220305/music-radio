package com.musicradio.radio.station.controller;

import com.musicradio.radio.common.response.ApiResponse;
import com.musicradio.radio.station.dto.StationResponse;
import com.musicradio.radio.station.service.StationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Public REST controller for accessing individual publicly available stations.
 */
@RestController
@RequestMapping("/api/v1/public/stations")
@RequiredArgsConstructor
public class PublicStationController {

    private final StationService stationService;

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<StationResponse>> getPublicStationById(@PathVariable Integer id) {
        StationResponse station = stationService.getPublicStationById(id);
        return ResponseEntity.ok(ApiResponse.success(station, "Station retrieved successfully"));
    }
}
