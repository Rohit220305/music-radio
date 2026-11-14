package com.musicradio.radio.theme.controller;

import com.musicradio.radio.common.response.ApiResponse;
import com.musicradio.radio.theme.dto.ThemeResponse;
import com.musicradio.radio.theme.service.ThemeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Public REST controller for browsing active themes and associated stations.
 */
@RestController
@RequestMapping("/api/v1/public/themes")
@RequiredArgsConstructor
public class PublicThemeController {

    private final ThemeService themeService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<ThemeResponse>>> getPublicThemes() {
        List<ThemeResponse> themes = themeService.getPublicThemes();
        return ResponseEntity.ok(ApiResponse.success(themes, "Active themes retrieved successfully"));
    }

    @GetMapping("/{slug}")
    public ResponseEntity<ApiResponse<ThemeResponse>> getPublicThemeBySlug(@PathVariable String slug) {
        ThemeResponse theme = themeService.getPublicThemeBySlug(slug);
        return ResponseEntity.ok(ApiResponse.success(theme, "Theme retrieved successfully"));
    }
}
