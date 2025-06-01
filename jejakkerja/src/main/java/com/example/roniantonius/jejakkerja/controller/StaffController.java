package com.example.roniantonius.jejakkerja.controller;

import com.example.roniantonius.jejakkerja.dto.ticket.TicketResponse;
import com.example.roniantonius.jejakkerja.dto.ticket.ValidateTicketRequest;
import com.example.roniantonius.jejakkerja.service.TicketService;
import jakarta.validation.Valid;
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
@RequestMapping("/api/v1/staff/events/{eventId}/tickets")
@RequiredArgsConstructor
@Validated
public class StaffController {

    private final TicketService ticketService;

    @PostMapping("/validate")
    @PreAuthorize("hasAuthority('ROLE_STAFF')")
    public ResponseEntity<TicketResponse> validateTicket(@PathVariable Long eventId,
                                                         @Valid @RequestBody ValidateTicketRequest request,
                                                         JwtAuthenticationToken principal) {
        TicketResponse ticketResponse = ticketService.validateTicket(eventId, request.getTicketCode(), principal);
        return ResponseEntity.ok(ticketResponse);
    }

    @GetMapping("/validated")
    @PreAuthorize("hasAuthority('ROLE_STAFF')")
    public ResponseEntity<Page<TicketResponse>> getValidatedTickets(@PathVariable Long eventId,
                                                                    JwtAuthenticationToken principal,
                                                                    @ParameterObject Pageable pageable) {
        Page<TicketResponse> validatedTickets = ticketService.getValidatedTicketsForEvent(eventId, principal, pageable);
        return ResponseEntity.ok(validatedTickets);
    }
}
