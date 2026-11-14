package com.musicradio.radio.theme.controller;

import com.musicradio.radio.common.dto.StatusUpdateRequest;
import com.musicradio.radio.common.exception.BadRequestException;
import com.musicradio.radio.common.exception.ResourceNotFoundException;
import com.musicradio.radio.common.response.ApiResponse;
import com.musicradio.radio.theme.dto.CreateThemeRequest;
import com.musicradio.radio.theme.dto.ThemeResponse;
import com.musicradio.radio.theme.dto.UpdateThemeRequest;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminThemeControllerTest {

    @Mock
    private ThemeService themeService;

    @InjectMocks
    private AdminThemeController adminThemeController;

    @Test
    @DisplayName("GET /api/v1/admin/themes should return 200 OK with all themes")
    void testGetAllThemesAdmin() {
        ThemeResponse theme = ThemeResponse.builder().id(1).name("Rainy Night").slug("rainy-night").build();
        when(themeService.getAllThemesAdmin()).thenReturn(Collections.singletonList(theme));

        ResponseEntity<ApiResponse<List<ThemeResponse>>> response = adminThemeController.getAllThemes();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
        assertEquals(1, response.getBody().getData().size());
        verify(themeService, times(1)).getAllThemesAdmin();
    }

    @Test
    @DisplayName("GET /api/v1/admin/themes/{id} should return 200 OK when found")
    void testGetThemeByIdSuccess() {
        ThemeResponse theme = ThemeResponse.builder().id(1).name("Rainy Night").slug("rainy-night").build();
        when(themeService.getThemeByIdAdmin(1)).thenReturn(theme);

        ResponseEntity<ApiResponse<ThemeResponse>> response = adminThemeController.getThemeById(1);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
        assertEquals("Rainy Night", response.getBody().getData().getName());
        verify(themeService, times(1)).getThemeByIdAdmin(1);
    }

    @Test
    @DisplayName("POST /api/v1/admin/themes should return 201 CREATED upon success")
    void testCreateThemeSuccess() {
        CreateThemeRequest request = CreateThemeRequest.builder()
                .name("New Theme")
                .slug("new-theme")
                .description("Desc")
                .backgroundImage("https://example.com/bg.jpg")
                .thumbnailImage("https://example.com/thumb.jpg")
                .sortOrder(5)
                .isActive(true)
                .build();

        ThemeResponse created = ThemeResponse.builder().id(2).name("New Theme").slug("new-theme").build();
        when(themeService.createTheme(any(CreateThemeRequest.class))).thenReturn(created);

        ResponseEntity<ApiResponse<ThemeResponse>> response = adminThemeController.createTheme(request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
        assertEquals("Theme created successfully", response.getBody().getMessage());
        assertEquals(2, response.getBody().getData().getId());
        verify(themeService, times(1)).createTheme(request);
    }

    @Test
    @DisplayName("PUT /api/v1/admin/themes/{id} should return 200 OK upon success")
    void testUpdateThemeSuccess() {
        UpdateThemeRequest request = UpdateThemeRequest.builder()
                .name("Updated Theme")
                .slug("updated-theme")
                .description("Updated Desc")
                .backgroundImage("https://example.com/bg2.jpg")
                .thumbnailImage("https://example.com/thumb2.jpg")
                .sortOrder(1)
                .isActive(true)
                .build();

        ThemeResponse updated = ThemeResponse.builder().id(1).name("Updated Theme").slug("updated-theme").build();
        when(themeService.updateTheme(eq(1), any(UpdateThemeRequest.class))).thenReturn(updated);

        ResponseEntity<ApiResponse<ThemeResponse>> response = adminThemeController.updateTheme(1, request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
        assertEquals("Theme updated successfully", response.getBody().getMessage());
        assertEquals("Updated Theme", response.getBody().getData().getName());
        verify(themeService, times(1)).updateTheme(1, request);
    }

    @Test
    @DisplayName("PATCH /api/v1/admin/themes/{id}/status should return 200 OK")
    void testUpdateThemeStatusSuccess() {
        StatusUpdateRequest request = StatusUpdateRequest.builder().isActive(false).build();
        ThemeResponse updated = ThemeResponse.builder().id(1).name("Rainy Night").slug("rainy-night").isActive(false).build();
        when(themeService.updateThemeStatus(1, request)).thenReturn(updated);

        ResponseEntity<ApiResponse<ThemeResponse>> response = adminThemeController.updateThemeStatus(1, request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
        assertEquals("Theme status updated successfully", response.getBody().getMessage());
        assertFalse(response.getBody().getData().getIsActive());
        verify(themeService, times(1)).updateThemeStatus(1, request);
    }

    @Test
    @DisplayName("DELETE /api/v1/admin/themes/{id} should return 200 OK upon success")
    void testDeleteThemeSuccess() {
        doNothing().when(themeService).deleteTheme(1);

        ResponseEntity<ApiResponse<Void>> response = adminThemeController.deleteTheme(1);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
        assertEquals("Theme deleted successfully", response.getBody().getMessage());
        verify(themeService, times(1)).deleteTheme(1);
    }

    @Test
    @DisplayName("DELETE /api/v1/admin/themes/{id} should propagate BadRequestException when theme has stations")
    void testDeleteThemeWithStationsConflict() {
        doThrow(new BadRequestException("Cannot delete theme that has associated stations"))
                .when(themeService).deleteTheme(1);

        assertThrows(BadRequestException.class, () -> adminThemeController.deleteTheme(1));
        verify(themeService, times(1)).deleteTheme(1);
    }
}
