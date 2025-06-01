package com.example.roniantonius.jejakkerja.controller;

import com.example.roniantonius.jejakkerja.dto.ticket.TicketResponse;
import com.example.roniantonius.jejakkerja.service.TicketService;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/events/{eventId}/sold-tickets")
@RequiredArgsConstructor
@Validated
public class TicketSalesController {

    private final TicketService ticketService;

    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_ORGANIZER')")
    public ResponseEntity<Page<TicketResponse>> getSoldTicketsForEvent(@PathVariable Long eventId,
                                                                       JwtAuthenticationToken principal,
                                                                       @ParameterObject Pageable pageable) {
        String organizerId = principal.getToken().getSubject();
        Page<TicketResponse> tickets = ticketService.getTicketsByEventForOrganizer(eventId, organizerId, pageable);
        return ResponseEntity.ok(tickets);
    }

    @GetMapping("/{ticketId}")
    @PreAuthorize("hasAuthority('ROLE_ORGANIZER')")
    public ResponseEntity<TicketResponse> getSoldTicketById(@PathVariable Long eventId,
                                                            @PathVariable Long ticketId,
                                                            JwtAuthenticationToken principal) {
        String organizerId = principal.getToken().getSubject();
        TicketResponse ticketResponse = ticketService.getTicketByIdForOrganizer(eventId, ticketId, organizerId);
        return ResponseEntity.ok(ticketResponse);
    }

    @PatchMapping("/{ticketId}/cancel")
    @PreAuthorize("hasAuthority('ROLE_ORGANIZER')")
    public ResponseEntity<TicketResponse> cancelTicket(@PathVariable Long eventId,
                                                       @PathVariable Long ticketId,
                                                       JwtAuthenticationToken principal) {
        String organizerId = principal.getToken().getSubject();
        TicketResponse ticketResponse = ticketService.cancelTicketByOrganizer(eventId, ticketId, organizerId);
        return ResponseEntity.ok(ticketResponse);
    }
}
