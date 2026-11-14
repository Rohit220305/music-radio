package com.musicradio.radio.theme.dto;

import com.musicradio.radio.station.dto.StationSummaryResponse;
import com.musicradio.radio.theme.entity.Theme;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Detailed response DTO representing a Theme including its associated stations.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ThemeResponse {

    private Integer id;
    private String name;
    private String slug;
    private String description;
    private String backgroundImage;
    private String thumbnailImage;
    private Boolean isActive;
    private Integer sortOrder;
    private Instant createdAt;
    private Instant updatedAt;
    private List<StationSummaryResponse> stations;

    public static ThemeResponse fromEntity(Theme theme) {
        return fromEntity(theme, false);
    }

    public static ThemeResponse fromEntity(Theme theme, boolean onlyActiveStations) {
        if (theme == null) {
            return null;
        }

        List<StationSummaryResponse> stationDtos = theme.getStations() != null
                ? theme.getStations().stream()
                        .filter(s -> !onlyActiveStations || Boolean.TRUE.equals(s.getIsActive()))
                        .map(s -> StationSummaryResponse.builder()
                                .id(s.getId())
                                .name(s.getName())
                                .slug(s.getSlug())
                                .youtubePlaylistUrl(s.getYoutubePlaylistUrl())
                                .build())
                        .collect(Collectors.toList())
                : Collections.emptyList();

        return ThemeResponse.builder()
                .id(theme.getId())
                .name(theme.getName())
                .slug(theme.getSlug())
                .description(theme.getDescription())
                .backgroundImage(theme.getBackgroundImage())
                .thumbnailImage(theme.getThumbnailImage())
                .isActive(theme.getIsActive())
                .sortOrder(theme.getSortOrder())
                .createdAt(theme.getCreatedAt())
                .updatedAt(theme.getUpdatedAt())
                .stations(stationDtos)
                .build();
    }
}

