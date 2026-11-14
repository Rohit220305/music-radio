package com.musicradio.radio.station.service;

import com.musicradio.radio.common.dto.StatusUpdateRequest;
import com.musicradio.radio.station.dto.CreateStationRequest;
import com.musicradio.radio.station.dto.StationResponse;
import com.musicradio.radio.station.dto.UpdateStationRequest;

import java.util.List;

/**
 * Service interface for Station management operations.
 */
public interface StationService {

    /**
     * Retrieve a specific publicly available station by Integer ID.
     * Enforces that BOTH the station and its parent theme are active.
     */
    StationResponse getPublicStationById(Integer id);

    /**
     * Retrieve all stations for admin management, optionally filtered by themeId.
     * Ordered deterministically by id ASC.
     */
    List<StationResponse> getAllStationsAdmin(Integer themeId);

    /**
     * Retrieve a specific station by Integer ID for admin management.
     */
    StationResponse getStationByIdAdmin(Integer id);

    /**
     * Create a new station associated with an existing theme.
     */
    StationResponse createStation(CreateStationRequest request);

    /**
     * Perform a full update of an existing station with bidirectional theme reassignment support.
     */
    StationResponse updateStation(Integer id, UpdateStationRequest request);

    /**
     * Update the active status of an existing station.
     */
    StationResponse updateStationStatus(Integer id, StatusUpdateRequest request);

    /**
     * Delete a station and cleanly disassociate it from its parent theme.
     */
    void deleteStation(Integer id);
}

