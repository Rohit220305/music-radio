package com.musicradio.radio.theme.entity;

import com.musicradio.radio.common.entity.BaseAuditableEntity;
import com.musicradio.radio.station.entity.Station;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * Entity representing an atmospheric mood/theme.
 * One Theme contains multiple Stations (1:N relationship).
 */
@Entity
@Table(name = "themes")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Theme extends BaseAuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name", nullable = false, length = 120)
    private String name;

    @Column(name = "slug", nullable = false, unique = true, length = 150)
    private String slug;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "background_image", length = 500)
    private String backgroundImage;

    @Column(name = "thumbnail_image", length = 500)
    private String thumbnailImage;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    @Column(name = "sort_order", nullable = false)
    @Builder.Default
    private Integer sortOrder = 0;

    @OneToMany(mappedBy = "theme", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @Builder.Default
    private List<Station> stations = new ArrayList<>();

    public void addStation(Station station) {
        if (station != null) {
            stations.add(station);
            station.setTheme(this);
        }
    }

    public void removeStation(Station station) {
        if (station != null) {
            stations.remove(station);
            // A Station cannot exist without a valid Theme (theme_id is NOT NULL).
            // Do not disassociate by setting theme to null.
        }
    }
}

