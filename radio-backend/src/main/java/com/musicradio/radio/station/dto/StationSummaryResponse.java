package com.musicradio.radio.station.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Lightweight summary of a station for embedding in theme responses.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StationSummaryResponse {

    private Integer id;
    private String name;
    private String slug;
    private String youtubePlaylistUrl;
}

