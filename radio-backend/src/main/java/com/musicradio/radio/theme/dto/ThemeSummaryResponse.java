package com.musicradio.radio.theme.dto;

import com.musicradio.radio.theme.entity.Theme;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

/**
 * Lightweight summary of a theme for list views and administrative dashboard widgets.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ThemeSummaryResponse {

    private Integer id;
    private String name;
    private String slug;
    private String description;
    private String thumbnailImage;
    private Integer sortOrder;
    private Boolean isActive;
    private int stationCount;
    private Instant updatedAt;

    public static ThemeSummaryResponse fromEntity(Theme theme) {
        if (theme == null) {
            return null;
        }
        return ThemeSummaryResponse.builder()
                .id(theme.getId())
                .name(theme.getName())
                .slug(theme.getSlug())
                .description(theme.getDescription())
                .thumbnailImage(theme.getThumbnailImage())
                .sortOrder(theme.getSortOrder())
                .isActive(theme.getIsActive())
                .stationCount(theme.getStations() != null ? theme.getStations().size() : 0)
                .updatedAt(theme.getUpdatedAt())
                .build();
    }
}
