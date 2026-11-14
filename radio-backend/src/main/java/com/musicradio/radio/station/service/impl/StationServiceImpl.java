package com.musicradio.radio.station.service.impl;

import com.musicradio.radio.common.dto.StatusUpdateRequest;
import com.musicradio.radio.common.exception.DuplicateResourceException;
import com.musicradio.radio.common.exception.ResourceNotFoundException;
import com.musicradio.radio.station.dto.CreateStationRequest;
import com.musicradio.radio.station.dto.StationResponse;
import com.musicradio.radio.station.dto.UpdateStationRequest;
import com.musicradio.radio.station.entity.Station;
import com.musicradio.radio.station.repository.StationRepository;
import com.musicradio.radio.station.service.StationService;
import com.musicradio.radio.theme.entity.Theme;
import com.musicradio.radio.theme.repository.ThemeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Production implementation of StationService.
 * Enforces transactional boundaries, dual-active availability, foreign key lookups,
 * bidirectional theme reassignment, and slug collision checks.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StationServiceImpl implements StationService {

    private final StationRepository stationRepository;
    private final ThemeRepository themeRepository;

    @Override
    public StationResponse getPublicStationById(Integer id) {
        log.debug("Fetching publicly available station by ID: {}", id);
        Station station = stationRepository.findPubliclyAvailableById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Station", "id", id));
        return StationResponse.fromEntity(station);
    }

    @Override
    public List<StationResponse> getAllStationsAdmin(Integer themeId) {
        log.debug("Fetching all stations for admin (filter themeId: {})", themeId);
        List<Station> stations;
        if (themeId != null) {
            if (!themeRepository.existsById(themeId)) {
                throw new ResourceNotFoundException("Theme", "id", themeId);
            }
            stations = stationRepository.findByThemeIdOrderByIdAsc(themeId);
        } else {
            stations = stationRepository.findAllByOrderByIdAsc();
        }
        return stations.stream()
                .map(StationResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public StationResponse getStationByIdAdmin(Integer id) {
        log.debug("Fetching station by ID: {}", id);
        Station station = stationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Station", "id", id));
        return StationResponse.fromEntity(station);
    }

    @Override
    @Transactional
    public StationResponse createStation(CreateStationRequest request) {
        Integer themeId = request.getThemeId();
        String slug = request.getSlug().trim();
        log.info("Creating new station with slug '{}' under theme ID {}", slug, themeId);

        Theme theme = themeRepository.findById(themeId)
                .orElseThrow(() -> new ResourceNotFoundException("Theme", "id", themeId));

        if (stationRepository.existsBySlug(slug)) {
            throw new DuplicateResourceException("Station", "slug", slug);
        }

        Station station = Station.builder()
                .theme(theme)
                .name(request.getName().trim())
                .slug(slug)
                .youtubePlaylistUrl(request.getYoutubePlaylistUrl().trim())
                .description(request.getDescription())
                .shuffleEnabled(request.getShuffleEnabled() != null ? request.getShuffleEnabled() : false)
                .repeatEnabled(request.getRepeatEnabled() != null ? request.getRepeatEnabled() : true)
                .isActive(request.getIsActive() != null ? request.getIsActive() : true)
                .build();

        theme.addStation(station);
        Station savedStation = stationRepository.save(station);
        log.info("Station created successfully with ID: {}", savedStation.getId());
        return StationResponse.fromEntity(savedStation);
    }

    @Override
    @Transactional
    public StationResponse updateStation(Integer id, UpdateStationRequest request) {
        log.info("Updating station ID: {}", id);
        Station existing = stationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Station", "id", id));

        String newSlug = request.getSlug().trim();
        if (stationRepository.existsBySlugAndIdNot(newSlug, id)) {
            throw new DuplicateResourceException("Station", "slug", newSlug);
        }

        // Consistent Bidirectional Theme Reassignment
        Integer targetThemeId = request.getThemeId();
        if (targetThemeId != null && !existing.getTheme().getId().equals(targetThemeId)) {
            log.info("Reassigning station ID {} from theme {} to theme {}",
                    id, existing.getTheme().getId(), targetThemeId);
            Theme newTheme = themeRepository.findById(targetThemeId)
                    .orElseThrow(() -> new ResourceNotFoundException("Theme", "id", targetThemeId));

            Theme oldTheme = existing.getTheme();
            if (oldTheme != null && oldTheme.getStations() != null) {
                oldTheme.getStations().remove(existing);
            }
            if (newTheme.getStations() != null) {
                newTheme.getStations().add(existing);
            }
            existing.setTheme(newTheme);
        }

        existing.setName(request.getName().trim());
        existing.setSlug(newSlug);
        existing.setYoutubePlaylistUrl(request.getYoutubePlaylistUrl().trim());
        existing.setDescription(request.getDescription());
        existing.setShuffleEnabled(request.getShuffleEnabled());
        existing.setRepeatEnabled(request.getRepeatEnabled());
        existing.setIsActive(request.getIsActive());

        Station updatedStation = stationRepository.save(existing);
        log.info("Station ID: {} updated successfully", id);
        return StationResponse.fromEntity(updatedStation);
    }

    @Override
    @Transactional
    public StationResponse updateStationStatus(Integer id, StatusUpdateRequest request) {
        log.info("Updating station status for ID: {} to isActive={}", id, request.getIsActive());
        Station existing = stationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Station", "id", id));

        existing.setIsActive(request.getIsActive());
        Station updatedStation = stationRepository.save(existing);
        return StationResponse.fromEntity(updatedStation);
    }

    @Override
    @Transactional
    public void deleteStation(Integer id) {
        log.info("Deleting station ID: {}", id);
        Station existing = stationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Station", "id", id));

        if (existing.getTheme() != null && existing.getTheme().getStations() != null) {
            existing.getTheme().getStations().remove(existing);
        }

        stationRepository.delete(existing);
        log.info("Station ID: {} deleted successfully", id);
    }
}

