package com.musicradio.radio.admin.service;

import com.musicradio.radio.admin.dto.DashboardStatsResponse;
import com.musicradio.radio.admin.service.impl.DashboardServiceImpl;
import com.musicradio.radio.station.repository.StationRepository;
import com.musicradio.radio.theme.entity.Theme;
import com.musicradio.radio.theme.repository.ThemeRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DashboardServiceTest {

    @Mock
    private ThemeRepository themeRepository;

    @Mock
    private StationRepository stationRepository;

    @InjectMocks
    private DashboardServiceImpl dashboardService;

    @Test
    @DisplayName("getDashboardStats should aggregate counts and return top 5 recent themes")
    void getDashboardStats_ShouldAggregateCorrectCounts() {
        Theme theme1 = Theme.builder()
                .id(1)
                .name("Lo-Fi Study")
                .slug("lo-fi-study")
                .isActive(true)
                .sortOrder(1)
                .stations(Collections.emptyList())
                .build();
        theme1.setUpdatedAt(Instant.now());

        when(themeRepository.count()).thenReturn(10L);
        when(themeRepository.countByIsActive(true)).thenReturn(8L);
        when(stationRepository.count()).thenReturn(25L);
        when(stationRepository.countByIsActive(true)).thenReturn(20L);
        when(themeRepository.findTop5ByOrderByUpdatedAtDescIdDesc()).thenReturn(List.of(theme1));

        DashboardStatsResponse stats = dashboardService.getDashboardStats();

        assertThat(stats).isNotNull();
        assertThat(stats.getTotalThemes()).isEqualTo(10L);
        assertThat(stats.getActiveThemes()).isEqualTo(8L);
        assertThat(stats.getInactiveThemes()).isEqualTo(2L);
        assertThat(stats.getTotalStations()).isEqualTo(25L);
        assertThat(stats.getActiveStations()).isEqualTo(20L);
        assertThat(stats.getInactiveStations()).isEqualTo(5L);
        assertThat(stats.getRecentlyUpdatedThemes()).hasSize(1);
        assertThat(stats.getRecentlyUpdatedThemes().get(0).getName()).isEqualTo("Lo-Fi Study");
    }

    @Test
    @DisplayName("getDashboardStats with empty database should return zero counts and empty list")
    void getDashboardStats_WithEmptyDatabase_ShouldReturnZeroes() {
        when(themeRepository.count()).thenReturn(0L);
        when(themeRepository.countByIsActive(true)).thenReturn(0L);
        when(stationRepository.count()).thenReturn(0L);
        when(stationRepository.countByIsActive(true)).thenReturn(0L);
        when(themeRepository.findTop5ByOrderByUpdatedAtDescIdDesc()).thenReturn(Collections.emptyList());

        DashboardStatsResponse stats = dashboardService.getDashboardStats();

        assertThat(stats).isNotNull();
        assertThat(stats.getTotalThemes()).isEqualTo(0L);
        assertThat(stats.getActiveThemes()).isEqualTo(0L);
        assertThat(stats.getInactiveThemes()).isEqualTo(0L);
        assertThat(stats.getTotalStations()).isEqualTo(0L);
        assertThat(stats.getActiveStations()).isEqualTo(0L);
        assertThat(stats.getInactiveStations()).isEqualTo(0L);
        assertThat(stats.getRecentlyUpdatedThemes()).isEmpty();
    }
}
