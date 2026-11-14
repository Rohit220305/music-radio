package com.musicradio.radio.station.repository;

import com.musicradio.radio.station.entity.Station;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA repository for Station entities.
 */
@Repository
public interface StationRepository extends JpaRepository<Station, Integer> {

    List<Station> findByThemeId(Integer themeId);

    Optional<Station> findBySlug(String slug);

    boolean existsBySlug(String slug);

    /**
     * Derived query method checking slug collision excluding the given station ID.
     */
    boolean existsBySlugAndIdNot(String slug, Integer id);

    boolean existsByThemeId(Integer themeId);

    List<Station> findByThemeIdAndIsActiveTrue(Integer themeId);

    /**
     * Retrieve stations by theme with deterministic ordering.
     */
    List<Station> findByThemeIdOrderByIdAsc(Integer themeId);

    /**
     * Retrieve all stations for admin with deterministic ordering.
     */
    List<Station> findAllByOrderByIdAsc();

    /**
     * Retrieve a specific publicly available station by Integer ID.
     * Enforces that BOTH the station and its parent theme must be active.
     */
    @Query("SELECT s FROM Station s JOIN s.theme t WHERE s.id = :id AND s.isActive = true AND t.isActive = true")
    Optional<Station> findPubliclyAvailableById(@Param("id") Integer id);

    /**
     * Retrieve all publicly available stations for a theme with deterministic ordering.
     * Enforces the business rule that BOTH the station and its parent theme must be active.
     */
    @Query("SELECT s FROM Station s JOIN s.theme t WHERE t.slug = :themeSlug AND s.isActive = true AND t.isActive = true ORDER BY s.id ASC")
    List<Station> findPubliclyAvailableByThemeSlug(@Param("themeSlug") String themeSlug);

    /**
     * Retrieve a specific publicly available station by its slug.
     * Enforces that BOTH the station and its parent theme must be active.
     */
    @Query("SELECT s FROM Station s JOIN s.theme t WHERE s.slug = :stationSlug AND s.isActive = true AND t.isActive = true")
    Optional<Station> findPubliclyAvailableBySlug(@Param("stationSlug") String stationSlug);
}
