package com.example.roniantonius.jejakkerja.domain.repository;

import com.example.roniantonius.jejakkerja.domain.entity.Event;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    List<Event> findByIsPublishedTrue(Pageable pageable);

    List<Event> findByOrganizerId(String organizerId, Pageable pageable);
}
