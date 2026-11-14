package com.musicradio.radio.station.dto;

import com.musicradio.radio.station.entity.Station;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

/**
 * Detailed response DTO representing a Station.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StationResponse {

    private Integer id;
    private Integer themeId;
    private String themeName;
    private String name;
    private String slug;
    private String youtubePlaylistUrl;
    private String description;
    private Boolean shuffleEnabled;
    private Boolean repeatEnabled;
    private Boolean isActive;
    private Instant createdAt;
    private Instant updatedAt;

    public static StationResponse fromEntity(Station station) {
        if (station == null) {
            return null;
        }
        return StationResponse.builder()
                .id(station.getId())
                .themeId(station.getTheme() != null ? station.getTheme().getId() : null)
                .themeName(station.getTheme() != null ? station.getTheme().getName() : null)
                .name(station.getName())
                .slug(station.getSlug())
                .youtubePlaylistUrl(station.getYoutubePlaylistUrl())
                .description(station.getDescription())
                .shuffleEnabled(station.getShuffleEnabled())
                .repeatEnabled(station.getRepeatEnabled())
                .isActive(station.getIsActive())
                .createdAt(station.getCreatedAt())
                .updatedAt(station.getUpdatedAt())
                .build();
    }
}

