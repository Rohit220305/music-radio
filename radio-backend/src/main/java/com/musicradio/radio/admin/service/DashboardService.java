package com.musicradio.radio.admin.service;

import com.musicradio.radio.admin.dto.DashboardStatsResponse;

/**
 * Service contract for calculating aggregated admin dashboard statistics.
 */
public interface DashboardService {

    /**
     * Compute total, active, and inactive counts for themes and stations,
     * alongside the top 5 most recently modified themes.
     *
     * @return DashboardStatsResponse
     */
    DashboardStatsResponse getDashboardStats();
}
