package com.musicradio.radio.theme.controller;

import com.musicradio.radio.common.dto.StatusUpdateRequest;
import com.musicradio.radio.common.response.ApiResponse;
import com.musicradio.radio.theme.dto.CreateThemeRequest;
import com.musicradio.radio.theme.dto.ThemeResponse;
import com.musicradio.radio.theme.dto.UpdateThemeRequest;
import com.musicradio.radio.theme.service.ThemeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Administrative REST controller for CRUD operations on Themes.
 */
@RestController
@RequestMapping("/api/v1/admin/themes")
@RequiredArgsConstructor
public class AdminThemeController {

    private final ThemeService themeService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<ThemeResponse>>> getAllThemes() {
        List<ThemeResponse> themes = themeService.getAllThemesAdmin();
        return ResponseEntity.ok(ApiResponse.success(themes, "Themes retrieved successfully"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ThemeResponse>> getThemeById(@PathVariable Integer id) {
        ThemeResponse theme = themeService.getThemeByIdAdmin(id);
        return ResponseEntity.ok(ApiResponse.success(theme, "Theme retrieved successfully"));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ThemeResponse>> createTheme(@Valid @RequestBody CreateThemeRequest request) {
        ThemeResponse created = themeService.createTheme(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(created, "Theme created successfully"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ThemeResponse>> updateTheme(
            @PathVariable Integer id,
            @Valid @RequestBody UpdateThemeRequest request) {
        ThemeResponse updated = themeService.updateTheme(id, request);
        return ResponseEntity.ok(ApiResponse.success(updated, "Theme updated successfully"));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<ThemeResponse>> updateThemeStatus(
            @PathVariable Integer id,
            @Valid @RequestBody StatusUpdateRequest request) {
        ThemeResponse updated = themeService.updateThemeStatus(id, request);
        return ResponseEntity.ok(ApiResponse.success(updated, "Theme status updated successfully"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteTheme(@PathVariable Integer id) {
        themeService.deleteTheme(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Theme deleted successfully"));
    }
}
