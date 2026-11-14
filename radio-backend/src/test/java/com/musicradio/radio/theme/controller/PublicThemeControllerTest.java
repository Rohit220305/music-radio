package com.musicradio.radio.theme.controller;

import com.musicradio.radio.common.exception.ResourceNotFoundException;
import com.musicradio.radio.common.response.ApiResponse;
import com.musicradio.radio.theme.dto.ThemeResponse;
import com.musicradio.radio.theme.service.ThemeService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PublicThemeControllerTest {

    @Mock
    private ThemeService themeService;

    @InjectMocks
    private PublicThemeController publicThemeController;

    @Test
    @DisplayName("GET /api/v1/public/themes should return 200 OK with active themes list")
    void testGetPublicThemes() {
        ThemeResponse theme = ThemeResponse.builder()
                .id(1)
                .name("Rainy Night")
                .slug("rainy-night")
                .isActive(true)
                .sortOrder(1)
                .stations(Collections.emptyList())
                .build();

        when(themeService.getPublicThemes()).thenReturn(Collections.singletonList(theme));

        ResponseEntity<ApiResponse<List<ThemeResponse>>> response = publicThemeController.getPublicThemes();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
        assertEquals("Active themes retrieved successfully", response.getBody().getMessage());
        assertEquals(1, response.getBody().getData().size());
        assertEquals("rainy-night", response.getBody().getData().get(0).getSlug());
        verify(themeService, times(1)).getPublicThemes();
    }

    @Test
    @DisplayName("GET /api/v1/public/themes/{slug} should return 200 OK when found")
    void testGetPublicThemeBySlugSuccess() {
        ThemeResponse theme = ThemeResponse.builder()
                .id(1)
                .name("Rainy Night")
                .slug("rainy-night")
                .isActive(true)
                .stations(Collections.emptyList())
                .build();

        when(themeService.getPublicThemeBySlug("rainy-night")).thenReturn(theme);

        ResponseEntity<ApiResponse<ThemeResponse>> response = publicThemeController.getPublicThemeBySlug("rainy-night");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
        assertEquals("Theme retrieved successfully", response.getBody().getMessage());
        assertEquals("rainy-night", response.getBody().getData().getSlug());
        verify(themeService, times(1)).getPublicThemeBySlug("rainy-night");
    }

    @Test
    @DisplayName("GET /api/v1/public/themes/{slug} should propagate ResourceNotFoundException when not found")
    void testGetPublicThemeBySlugNotFound() {
        when(themeService.getPublicThemeBySlug("unknown"))
                .thenThrow(new ResourceNotFoundException("Theme", "slug", "unknown"));

        assertThrows(ResourceNotFoundException.class, () -> publicThemeController.getPublicThemeBySlug("unknown"));
        verify(themeService, times(1)).getPublicThemeBySlug("unknown");
    }
}
