package com.musicradio.radio.station.dto;

import com.musicradio.radio.station.validation.ValidYouTubePlaylistUrl;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Request DTO for full update (PUT) of an existing Station.
 * Enforces required fields for full resource replacement and supports theme reassignment.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateStationRequest {

    @NotNull(message = "Theme ID is required")
    private Integer themeId;

    @NotBlank(message = "Station name is required")
    @Size(max = 150, message = "Station name must not exceed 150 characters")
    private String name;

    @NotBlank(message = "Station slug is required")
    @Size(max = 150, message = "Station slug must not exceed 150 characters")
    @Pattern(regexp = "^[a-z0-9-]+$", message = "Slug must contain only lowercase alphanumeric characters and hyphens")
    private String slug;

    @NotBlank(message = "YouTube playlist URL is required")
    @Size(max = 1000, message = "YouTube playlist URL must not exceed 1000 characters")
    @ValidYouTubePlaylistUrl
    private String youtubePlaylistUrl;

    @Size(max = 2000, message = "Description must not exceed 2000 characters")
    private String description;

    @NotNull(message = "Shuffle enabled flag is required")
    private Boolean shuffleEnabled;

    @NotNull(message = "Repeat enabled flag is required")
    private Boolean repeatEnabled;

    @NotNull(message = "Active status is required")
    private Boolean isActive;
}
