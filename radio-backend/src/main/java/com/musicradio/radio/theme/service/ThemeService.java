package com.musicradio.radio.theme.service;

import com.musicradio.radio.common.dto.StatusUpdateRequest;
import com.musicradio.radio.theme.dto.CreateThemeRequest;
import com.musicradio.radio.theme.dto.ThemeResponse;
import com.musicradio.radio.theme.dto.UpdateThemeRequest;

import java.util.List;

/**
 * Service interface for Theme management operations.
 */
public interface ThemeService {

    /**
     * Retrieve all active themes for public consumption with active stations.
     * Ordered deterministically by sortOrder ASC, id ASC.
     */
    List<ThemeResponse> getPublicThemes();

    /**
     * Retrieve an active theme by its unique slug with active stations.
     */
    ThemeResponse getPublicThemeBySlug(String slug);

    /**
     * Retrieve all themes for admin management (including inactive) with all stations.
     * Ordered deterministically by sortOrder ASC, id ASC.
     */
    List<ThemeResponse> getAllThemesAdmin();

    /**
     * Retrieve a specific theme by Integer ID for admin management with all stations.
     */
    ThemeResponse getThemeByIdAdmin(Integer id);

    /**
     * Create a new theme with unique slug validation.
     */
    ThemeResponse createTheme(CreateThemeRequest request);

    /**
     * Perform a full update of an existing theme.
     */
    ThemeResponse updateTheme(Integer id, UpdateThemeRequest request);

    /**
     * Update the active status of an existing theme.
     */
    ThemeResponse updateThemeStatus(Integer id, StatusUpdateRequest request);

    /**
     * Delete a theme if no stations are currently attached.
     */
    void deleteTheme(Integer id);
}

