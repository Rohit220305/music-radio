package com.musicradio.radio.station.entity;

import com.musicradio.radio.common.entity.BaseAuditableEntity;
import com.musicradio.radio.theme.entity.Theme;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entity representing a radio station attached to a Theme and playing a YouTube playlist URL.
 */
@Entity
@Table(name = "stations")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Station extends BaseAuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "theme_id", nullable = false)
    private Theme theme;

    @Column(name = "name", nullable = false, length = 150)
    private String name;

    @Column(name = "slug", nullable = false, unique = true, length = 150)
    private String slug;

    @Column(name = "youtube_playlist_url", nullable = false, length = 1000)
    private String youtubePlaylistUrl;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "shuffle_enabled", nullable = false)
    @Builder.Default
    private Boolean shuffleEnabled = false;

    @Column(name = "repeat_enabled", nullable = false)
    @Builder.Default
    private Boolean repeatEnabled = true;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;
}

