package com.musicradio.radio.theme.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Request DTO for creating a new Theme.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateThemeRequest {

    @NotBlank(message = "Theme name is required")
    @Size(max = 120, message = "Theme name must not exceed 120 characters")
    private String name;

    @NotBlank(message = "Theme slug is required")
    @Size(max = 150, message = "Theme slug must not exceed 150 characters")
    @Pattern(regexp = "^[a-z0-9-]+$", message = "Slug must contain only lowercase alphanumeric characters and hyphens")
    private String slug;

    @Size(max = 2000, message = "Description must not exceed 2000 characters")
    private String description;

    @Size(max = 500, message = "Background image URL must not exceed 500 characters")
    private String backgroundImage;

    @Size(max = 500, message = "Thumbnail image URL must not exceed 500 characters")
    private String thumbnailImage;

    @Builder.Default
    private Integer sortOrder = 0;

    @Builder.Default
    private Boolean isActive = true;
}

