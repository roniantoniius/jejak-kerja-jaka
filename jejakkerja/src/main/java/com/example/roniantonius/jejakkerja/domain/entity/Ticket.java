package com.example.roniantonius.jejakkerja.domain.entity;

import jakarta.persistence.*;
import lombok.*;
// import org.hibernate.annotations.ColumnDefault; // Not needed for status

import java.time.LocalDateTime;

@Entity
@Table(name = "tickets")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Ticket extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String ticketCode; // Unique code for the ticket, possibly UUID

    @Column(nullable = false)
    private LocalDateTime purchaseTime;

    // isValidated is removed
    // @ColumnDefault("false")
    // private boolean isValidated = false;

    @Column(nullable = true)
    private LocalDateTime validationTime; // Set when status becomes VALIDATED

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private TicketStatus status = TicketStatus.ACTIVE;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ticket_type_id", nullable = false)
    private TicketType ticketType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "attendee_id", nullable = false)
    private AppUser attendee;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "validated_by_staff_id", nullable = true)
    private AppUser validatedBy; // Staff who validated the ticket
}
