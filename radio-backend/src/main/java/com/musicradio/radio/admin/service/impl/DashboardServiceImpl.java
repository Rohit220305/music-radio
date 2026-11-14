package com.musicradio.radio.admin.service.impl;

import com.musicradio.radio.admin.dto.DashboardStatsResponse;
import com.musicradio.radio.admin.service.DashboardService;
import com.musicradio.radio.station.repository.StationRepository;
import com.musicradio.radio.theme.dto.ThemeSummaryResponse;
import com.musicradio.radio.theme.repository.ThemeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service implementation for aggregating administrative dashboard metrics.
 * Uses database-level COUNT and indexed top-5 queries to ensure O(1) in-memory overhead and prevent N+1 queries.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DashboardServiceImpl implements DashboardService {

    private final ThemeRepository themeRepository;
    private final StationRepository stationRepository;

    @Override
    public DashboardStatsResponse getDashboardStats() {
        long totalThemes = themeRepository.count();
        long activeThemes = themeRepository.countByIsActive(true);
        long inactiveThemes = Math.max(0, totalThemes - activeThemes);

        long totalStations = stationRepository.count();
        long activeStations = stationRepository.countByIsActive(true);
        long inactiveStations = Math.max(0, totalStations - activeStations);

        List<ThemeSummaryResponse> recentThemes = themeRepository.findTop5ByOrderByUpdatedAtDescIdDesc()
                .stream()
                .map(ThemeSummaryResponse::fromEntity)
                .toList();

        return DashboardStatsResponse.builder()
                .totalThemes(totalThemes)
                .activeThemes(activeThemes)
                .inactiveThemes(inactiveThemes)
                .totalStations(totalStations)
                .activeStations(activeStations)
                .inactiveStations(inactiveStations)
                .recentlyUpdatedThemes(recentThemes)
                .build();
    }
}
