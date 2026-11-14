package com.musicradio.radio.admin.dto;

import com.musicradio.radio.theme.dto.ThemeSummaryResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * Aggregated administrative metrics for themes, stations, and recently updated themes.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardStatsResponse {

    private long totalThemes;
    private long activeThemes;
    private long inactiveThemes;

    private long totalStations;
    private long activeStations;
    private long inactiveStations;

    private List<ThemeSummaryResponse> recentlyUpdatedThemes;
}
