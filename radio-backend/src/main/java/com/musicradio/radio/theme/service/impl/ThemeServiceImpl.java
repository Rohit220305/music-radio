package com.musicradio.radio.theme.service.impl;

import com.musicradio.radio.common.dto.StatusUpdateRequest;
import com.musicradio.radio.common.exception.BadRequestException;
import com.musicradio.radio.common.exception.DuplicateResourceException;
import com.musicradio.radio.common.exception.ResourceNotFoundException;
import com.musicradio.radio.station.repository.StationRepository;
import com.musicradio.radio.theme.dto.CreateThemeRequest;
import com.musicradio.radio.theme.dto.ThemeResponse;
import com.musicradio.radio.theme.dto.UpdateThemeRequest;
import com.musicradio.radio.theme.entity.Theme;
import com.musicradio.radio.theme.repository.ThemeRepository;
import com.musicradio.radio.theme.service.ThemeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Production implementation of ThemeService.
 * Enforces transactional boundaries, dual-active filtering, slug collisions, and referential safety.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ThemeServiceImpl implements ThemeService {

    private final ThemeRepository themeRepository;
    private final StationRepository stationRepository;

    @Override
    public List<ThemeResponse> getPublicThemes() {
        log.debug("Fetching all active themes for public consumption");
        List<Theme> themes = themeRepository.findByIsActiveTrueOrderBySortOrderAscIdAsc();
        return themes.stream()
                .map(theme -> ThemeResponse.fromEntity(theme, true))
                .collect(Collectors.toList());
    }

    @Override
    public ThemeResponse getPublicThemeBySlug(String slug) {
        log.debug("Fetching public theme by slug: {}", slug);
        Theme theme = themeRepository.findBySlugAndIsActiveTrue(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Theme", "slug", slug));
        return ThemeResponse.fromEntity(theme, true);
    }

    @Override
    public List<ThemeResponse> getAllThemesAdmin() {
        log.debug("Fetching all themes for admin management");
        List<Theme> themes = themeRepository.findAllWithStations();
        return themes.stream()
                .map(theme -> ThemeResponse.fromEntity(theme, false))
                .collect(Collectors.toList());
    }

    @Override
    public ThemeResponse getThemeByIdAdmin(Integer id) {
        log.debug("Fetching theme by id: {}", id);
        Theme theme = themeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Theme", "id", id));
        return ThemeResponse.fromEntity(theme, false);
    }

    @Override
    @Transactional
    public ThemeResponse createTheme(CreateThemeRequest request) {
        String slug = request.getSlug().trim();
        log.info("Creating new theme with slug: {}", slug);

        if (themeRepository.existsBySlug(slug)) {
            throw new DuplicateResourceException("Theme", "slug", slug);
        }

        Theme theme = Theme.builder()
                .name(request.getName().trim())
                .slug(slug)
                .description(request.getDescription())
                .backgroundImage(request.getBackgroundImage())
                .thumbnailImage(request.getThumbnailImage())
                .sortOrder(request.getSortOrder() != null ? request.getSortOrder() : 0)
                .isActive(request.getIsActive() != null ? request.getIsActive() : true)
                .build();

        Theme savedTheme = themeRepository.save(theme);
        log.info("Theme created successfully with ID: {}", savedTheme.getId());
        return ThemeResponse.fromEntity(savedTheme, false);
    }

    @Override
    @Transactional
    public ThemeResponse updateTheme(Integer id, UpdateThemeRequest request) {
        log.info("Updating theme ID: {}", id);
        Theme existing = themeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Theme", "id", id));

        String newSlug = request.getSlug().trim();
        if (themeRepository.existsBySlugAndIdNot(newSlug, id)) {
            throw new DuplicateResourceException("Theme", "slug", newSlug);
        }

        existing.setName(request.getName().trim());
        existing.setSlug(newSlug);
        existing.setDescription(request.getDescription());
        existing.setBackgroundImage(request.getBackgroundImage());
        existing.setThumbnailImage(request.getThumbnailImage());
        existing.setSortOrder(request.getSortOrder());
        existing.setIsActive(request.getIsActive());

        Theme updatedTheme = themeRepository.save(existing);
        log.info("Theme ID: {} updated successfully", id);
        return ThemeResponse.fromEntity(updatedTheme, false);
    }

    @Override
    @Transactional
    public ThemeResponse updateThemeStatus(Integer id, StatusUpdateRequest request) {
        log.info("Updating theme status for ID: {} to isActive={}", id, request.getIsActive());
        Theme existing = themeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Theme", "id", id));

        existing.setIsActive(request.getIsActive());
        Theme updatedTheme = themeRepository.save(existing);
        return ThemeResponse.fromEntity(updatedTheme, false);
    }

    @Override
    @Transactional
    public void deleteTheme(Integer id) {
        log.info("Deleting theme ID: {}", id);
        Theme existing = themeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Theme", "id", id));

        if (stationRepository.existsByThemeId(id)) {
            throw new BadRequestException("Cannot delete theme with ID " + id + " because it contains associated stations. Reassign or delete stations first.");
        }

        themeRepository.delete(existing);
        log.info("Theme ID: {} deleted successfully", id);
    }
}

