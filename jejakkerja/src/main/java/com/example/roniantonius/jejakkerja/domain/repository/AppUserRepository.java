package com.example.roniantonius.jejakkerja.domain.repository;

import com.example.roniantonius.jejakkerja.domain.entity.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AppUserRepository extends JpaRepository<AppUser, Long> {

    Optional<AppUser> findByKeycloakId(String keycloakId);

    Optional<AppUser> findByUsername(String username);

    Optional<AppUser> findByEmail(String email);
}
