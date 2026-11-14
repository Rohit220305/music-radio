package com.musicradio.radio.theme.repository;

import com.musicradio.radio.theme.entity.Theme;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA repository for Theme entities.
 */
@Repository
public interface ThemeRepository extends JpaRepository<Theme, Integer> {

    Optional<Theme> findBySlug(String slug);

    boolean existsBySlug(String slug);

    /**
     * Derived query method checking slug collision excluding the given theme ID.
     */
    boolean existsBySlugAndIdNot(String slug, Integer id);

    List<Theme> findByIsActiveTrueOrderBySortOrderAsc();

    /**
     * Retrieve active themes with deterministic ordering (sortOrder ASC, id ASC).
     * Uses @EntityGraph to eagerly fetch stations in a single query, preventing N+1 queries.
     */
    @EntityGraph(attributePaths = {"stations"})
    List<Theme> findByIsActiveTrueOrderBySortOrderAscIdAsc();

    /**
     * Retrieve active theme by slug with eagerly loaded stations.
     */
    @EntityGraph(attributePaths = {"stations"})
    Optional<Theme> findBySlugAndIsActiveTrue(String slug);

    /**
     * Retrieve all themes for admin with deterministic ordering and eagerly loaded stations.
     */
    @EntityGraph(attributePaths = {"stations"})
    @Query("SELECT DISTINCT t FROM Theme t ORDER BY t.sortOrder ASC, t.id ASC")
    List<Theme> findAllWithStations();
}
