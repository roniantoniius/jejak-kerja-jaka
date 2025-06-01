package com.example.roniantonius.jejakkerja.dto.ticket;

import lombok.AllArgsConstructor;
import lombok.Builder;
import com.example.roniantonius.jejakkerja.domain.entity.TicketStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TicketResponse {
    private Long id;
    private String ticketCode;
    private LocalDateTime purchaseTime;
    // private boolean isValidated; // Removed
    private LocalDateTime validationTime;
    private TicketStatus status; // Added
    private Long eventId;
    private Long ticketTypeId;
    private Long attendeeId;
    private Long validatedByStaffId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
