package com.example.roniantonius.jejakkerja.service;

import com.example.roniantonius.jejakkerja.dto.ticket.TicketResponse;
import com.example.roniantonius.jejakkerja.domain.entity.AppUser;
import com.example.roniantonius.jejakkerja.dto.ticket.TicketResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

// Removed java.util.List as Page is used

public interface TicketService {

    // Organizer flows
    Page<TicketResponse> getTicketsByEventForOrganizer(Long eventId, String organizerId, Pageable pageable);
    TicketResponse getTicketByIdForOrganizer(Long eventId, Long ticketId, String organizerId);
    TicketResponse cancelTicketByOrganizer(Long eventId, Long ticketId, String organizerId);

    // Attendee flows
    TicketResponse purchaseTicket(Long eventId, Long ticketTypeId, JwtAuthenticationToken principal);
    Page<TicketResponse> getMyTickets(Long attendeeAppUserId, Pageable pageable);
    TicketResponse getMyTicketById(Long ticketId, Long attendeeAppUserId);
    byte[] getTicketQrCode(Long ticketId, Long attendeeAppUserId) throws Exception; // Added throws Exception for QR generation

    // Staff flows
    TicketResponse validateTicket(Long eventId, String ticketCode, JwtAuthenticationToken staffPrincipal);
    Page<TicketResponse> getValidatedTicketsForEvent(Long eventId, JwtAuthenticationToken staffPrincipal, Pageable pageable);
}
