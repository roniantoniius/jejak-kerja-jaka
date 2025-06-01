package com.example.roniantonius.jejakkerja.domain.repository;

import com.example.roniantonius.jejakkerja.domain.entity.Ticket;
import com.example.roniantonius.jejakkerja.domain.entity.TicketStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {

    Page<Ticket> findByEventId(Long eventId, Pageable pageable); // Changed for pagination

    Page<Ticket> findByAttendeeId(Long attendeeId, Pageable pageable); // Changed for pagination

    Optional<Ticket> findByTicketCode(String ticketCode);

    Page<Ticket> findByEventIdAndStatus(Long eventId, TicketStatus status, Pageable pageable);
}
