package com.musicradio.radio.theme.dto;

import com.musicradio.radio.theme.entity.Theme;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Lightweight summary of a theme for list views.
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
                .build();
    }
}

