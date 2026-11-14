package com.musicradio.radio.admin.controller;

import com.musicradio.radio.admin.dto.DashboardStatsResponse;
import com.musicradio.radio.admin.service.DashboardService;
import com.musicradio.radio.common.response.ApiResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdminDashboardControllerTest {

    @Mock
    private DashboardService dashboardService;

    @InjectMocks
    private AdminDashboardController adminDashboardController;

    @Test
    @DisplayName("getDashboardStats returns 200 OK and dashboard statistics")
    void getDashboardStats_ShouldReturnOkWithStats() {
        DashboardStatsResponse stats = DashboardStatsResponse.builder()
                .totalThemes(5)
                .activeThemes(4)
                .inactiveThemes(1)
                .totalStations(12)
                .activeStations(10)
                .inactiveStations(2)
                .recentlyUpdatedThemes(Collections.emptyList())
                .build();

        when(dashboardService.getDashboardStats()).thenReturn(stats);

        ResponseEntity<ApiResponse<DashboardStatsResponse>> result = adminDashboardController.getDashboardStats();

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isNotNull();
        assertThat(result.getBody().isSuccess()).isTrue();
        assertThat(result.getBody().getMessage()).isEqualTo("Dashboard statistics retrieved successfully");
        assertThat(result.getBody().getData().getTotalThemes()).isEqualTo(5);
        assertThat(result.getBody().getData().getActiveThemes()).isEqualTo(4);
        assertThat(result.getBody().getData().getTotalStations()).isEqualTo(12);
    }
}
