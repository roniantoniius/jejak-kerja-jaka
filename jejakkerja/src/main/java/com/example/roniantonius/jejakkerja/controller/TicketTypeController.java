package com.example.roniantonius.jejakkerja.controller;

import com.example.roniantonius.jejakkerja.dto.tickertype.CreateTicketTypeRequest;
import com.example.roniantonius.jejakkerja.dto.tickertype.TicketTypeResponse;
import com.example.roniantonius.jejakkerja.dto.tickertype.UpdateTicketTypeRequest;
import com.example.roniantonius.jejakkerja.service.TicketTypeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/events/{eventId}/ticket-types")
@RequiredArgsConstructor
@Validated
public class TicketTypeController {

    private final TicketTypeService ticketTypeService;

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_ORGANIZER')")
    public ResponseEntity<TicketTypeResponse> createTicketType(@PathVariable Long eventId,
                                                               @Valid @RequestBody CreateTicketTypeRequest request,
                                                               JwtAuthenticationToken principal) {
        String organizerId = principal.getToken().getSubject();
        TicketTypeResponse ticketTypeResponse = ticketTypeService.createTicketType(eventId, request, organizerId);
        return new ResponseEntity<>(ticketTypeResponse, HttpStatus.CREATED);
    }

    @GetMapping("/{ticketTypeId}")
    @PreAuthorize("hasAuthority('ROLE_ORGANIZER')")
    public ResponseEntity<TicketTypeResponse> getTicketTypeById(@PathVariable Long eventId,
                                                                @PathVariable Long ticketTypeId,
                                                                JwtAuthenticationToken principal) {
        String organizerId = principal.getToken().getSubject();
        TicketTypeResponse ticketTypeResponse = ticketTypeService.getTicketTypeById(eventId, ticketTypeId, organizerId);
        return ResponseEntity.ok(ticketTypeResponse);
    }

    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_ORGANIZER')")
    public ResponseEntity<List<TicketTypeResponse>> getAllTicketTypesByEvent(@PathVariable Long eventId,
                                                                            JwtAuthenticationToken principal) {
        String organizerId = principal.getToken().getSubject();
        List<TicketTypeResponse> ticketTypes = ticketTypeService.getAllTicketTypesByEvent(eventId, organizerId);
        return ResponseEntity.ok(ticketTypes);
    }

    @PatchMapping("/{ticketTypeId}") // Changed from PUT to PATCH
    @PreAuthorize("hasAuthority('ROLE_ORGANIZER')")
    public ResponseEntity<TicketTypeResponse> updateTicketType(@PathVariable Long eventId,
                                                               @PathVariable Long ticketTypeId,
                                                               @Valid @RequestBody UpdateTicketTypeRequest request,
                                                               JwtAuthenticationToken principal) {
        String organizerId = principal.getToken().getSubject();
        TicketTypeResponse updatedTicketType = ticketTypeService.updateTicketType(eventId, ticketTypeId, request, organizerId);
        return ResponseEntity.ok(updatedTicketType);
    }

    @DeleteMapping("/{ticketTypeId}")
    @PreAuthorize("hasAuthority('ROLE_ORGANIZER')")
    public ResponseEntity<Void> deleteTicketType(@PathVariable Long eventId,
                                                 @PathVariable Long ticketTypeId,
                                                 JwtAuthenticationToken principal) {
        String organizerId = principal.getToken().getSubject();
        ticketTypeService.deleteTicketType(eventId, ticketTypeId, organizerId);
        return ResponseEntity.noContent().build();
    }
}
