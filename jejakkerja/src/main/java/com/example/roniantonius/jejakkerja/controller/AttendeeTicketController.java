package com.example.roniantonius.jejakkerja.controller;

import com.example.roniantonius.jejakkerja.domain.entity.AppUser;
import com.example.roniantonius.jejakkerja.dto.ticket.TicketResponse;
import com.example.roniantonius.jejakkerja.service.AppUserService;
import com.example.roniantonius.jejakkerja.service.TicketService;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Validated
public class AttendeeTicketController {

    private final TicketService ticketService;
    private final AppUserService appUserService;

    @PostMapping("/events/{eventId}/ticket-types/{ticketTypeId}/purchase")
    @PreAuthorize("hasAuthority('ROLE_USER') or hasAuthority('ROLE_ATTENDEE')") // Assuming ROLE_USER or ROLE_ATTENDEE for attendees
    public ResponseEntity<TicketResponse> purchaseTicket(@PathVariable Long eventId,
                                                         @PathVariable Long ticketTypeId,
                                                         JwtAuthenticationToken principal) {
        // AppUserService is used within TicketService's purchaseTicket method
        TicketResponse ticketResponse = ticketService.purchaseTicket(eventId, ticketTypeId, principal);
        return new ResponseEntity<>(ticketResponse, HttpStatus.CREATED);
    }

    @GetMapping("/my-tickets")
    @PreAuthorize("hasAuthority('ROLE_USER') or hasAuthority('ROLE_ATTENDEE')")
    public ResponseEntity<Page<TicketResponse>> getMyTickets(JwtAuthenticationToken principal,
                                                             @ParameterObject Pageable pageable) {
        AppUser attendee = appUserService.findOrCreateAppUserEntity(principal);
        Page<TicketResponse> tickets = ticketService.getMyTickets(attendee.getId(), pageable);
        return ResponseEntity.ok(tickets);
    }

    @GetMapping("/my-tickets/{ticketId}")
    @PreAuthorize("hasAuthority('ROLE_USER') or hasAuthority('ROLE_ATTENDEE')")
    public ResponseEntity<TicketResponse> getMyTicketById(@PathVariable Long ticketId,
                                                          JwtAuthenticationToken principal) {
        AppUser attendee = appUserService.findOrCreateAppUserEntity(principal);
        TicketResponse ticketResponse = ticketService.getMyTicketById(ticketId, attendee.getId());
        return ResponseEntity.ok(ticketResponse);
    }

    @GetMapping("/my-tickets/{ticketId}/qr-code")
    @PreAuthorize("hasAuthority('ROLE_USER') or hasAuthority('ROLE_ATTENDEE')")
    public ResponseEntity<byte[]> getTicketQrCode(@PathVariable Long ticketId,
                                                  JwtAuthenticationToken principal) {
        AppUser attendee = appUserService.findOrCreateAppUserEntity(principal);
        try {
            byte[] qrCode = ticketService.getTicketQrCode(ticketId, attendee.getId());
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_PNG);
            headers.setContentLength(qrCode.length);
            return new ResponseEntity<>(qrCode, headers, HttpStatus.OK);
        } catch (Exception e) {
            // Log error e
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
