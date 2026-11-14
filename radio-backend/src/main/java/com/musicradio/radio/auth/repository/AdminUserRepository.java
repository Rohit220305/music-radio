package com.musicradio.radio.auth.repository;

import com.musicradio.radio.auth.entity.AdminUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Spring Data JPA repository for AdminUser entities.
 */
@Repository
public interface AdminUserRepository extends JpaRepository<AdminUser, Integer> {

    Optional<AdminUser> findByEmail(String email);

    boolean existsByEmail(String email);
}

