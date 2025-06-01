package com.example.roniantonius.jejakkerja.service.impl;

import com.example.roniantonius.jejakkerja.domain.entity.Event;
import com.example.roniantonius.jejakkerja.domain.entity.Ticket;
import com.example.roniantonius.jejakkerja.domain.entity.TicketStatus;
import com.example.roniantonius.jejakkerja.domain.entity.TicketType;
import com.example.roniantonius.jejakkerja.domain.entity.*;
import com.example.roniantonius.jejakkerja.domain.repository.EventRepository;
import com.example.roniantonius.jejakkerja.domain.repository.TicketRepository;
import com.example.roniantonius.jejakkerja.domain.repository.TicketTypeRepository;
// AppUserRepository will be needed for AppUserService, not directly here if AppUserService is used
import com.example.roniantonius.jejakkerja.dto.ticket.TicketResponse;
import com.example.roniantonius.jejakkerja.exception.ResourceNotFoundException;
import com.example.roniantonius.jejakkerja.mapper.TicketMapper;
import com.example.roniantonius.jejakkerja.service.AppUserService;
import com.example.roniantonius.jejakkerja.service.TicketService;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class TicketServiceImpl implements TicketService {

    private final TicketRepository ticketRepository;
    private final TicketMapper ticketMapper;
    private final EventRepository eventRepository;
    private final TicketTypeRepository ticketTypeRepository;
    private final AppUserService appUserService; // Added AppUserService

    // Organizer specific helper
    private Event getEventIfOrganizer(Long eventId, String organizerId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event", "id", eventId));
        if (!Objects.equals(event.getOrganizerId(), organizerId)) {
            throw new AccessDeniedException("You are not authorized to access tickets for this event.");
        }
        return event;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TicketResponse> getTicketsByEventForOrganizer(Long eventId, String organizerId, Pageable pageable) {
        getEventIfOrganizer(eventId, organizerId); // Check ownership
        Page<Ticket> ticketsPage = ticketRepository.findByEventId(eventId, pageable);
        return ticketsPage.map(ticketMapper::toTicketResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public TicketResponse getTicketByIdForOrganizer(Long eventId, Long ticketId, String organizerId) {
        getEventIfOrganizer(eventId, organizerId); // Check ownership
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket", "id", ticketId));
        if (!Objects.equals(ticket.getEvent().getId(), eventId)) {
            throw new ResourceNotFoundException("Ticket with id " + ticketId + " not found for event " + eventId);
        }
        return ticketMapper.toTicketResponse(ticket);
    }

    @Override
    public TicketResponse cancelTicketByOrganizer(Long eventId, Long ticketId, String organizerId) {
        getEventIfOrganizer(eventId, organizerId); // Check ownership
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket", "id", ticketId));

        if (!Objects.equals(ticket.getEvent().getId(), eventId)) {
            throw new ResourceNotFoundException("Ticket with id " + ticketId + " not found for event " + eventId);
        }

        if (ticket.getStatus() == TicketStatus.VALIDATED) {
            throw new IllegalStateException("Cannot cancel a validated ticket.");
        }
        if (ticket.getStatus() == TicketStatus.CANCELLED) {
            throw new IllegalStateException("Ticket is already cancelled.");
        }

        ticket.setStatus(TicketStatus.CANCELLED);
        // Potentially set other fields like a cancellationTimestamp if needed

        TicketType ticketType = ticket.getTicketType();
        if (ticketType != null) {
            ticketType.setRemainingQuantity(ticketType.getRemainingQuantity() + 1);
            ticketTypeRepository.save(ticketType);
        }

        Ticket savedTicket = ticketRepository.save(ticket);
        return ticketMapper.toTicketResponse(savedTicket);
    }

    // Attendee specific methods
    @Override
    public TicketResponse purchaseTicket(Long eventId, Long ticketTypeId, JwtAuthenticationToken principal) {
        AppUser attendee = appUserService.findOrCreateAppUserEntity(principal);

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event", "id", eventId));
        if (!event.isPublished()) {
            throw new IllegalStateException("Event is not available for ticket purchase.");
        }

        TicketType ticketType = ticketTypeRepository.findById(ticketTypeId)
                .orElseThrow(() -> new ResourceNotFoundException("TicketType", "id", ticketTypeId));
        if (!Objects.equals(ticketType.getEvent().getId(), eventId)) {
            throw new ResourceNotFoundException("TicketType with id " + ticketTypeId + " not found for event " + eventId);
        }

        if (ticketType.getRemainingQuantity() <= 0) {
            throw new IllegalStateException("Tickets for this type are sold out.");
        }

        // Critical section: Decrement quantity and save. Consider optimistic/pessimistic locking for high concurrency.
        ticketType.setRemainingQuantity(ticketType.getRemainingQuantity() - 1);
        ticketTypeRepository.save(ticketType);

        Ticket newTicket = Ticket.builder()
                .event(event)
                .ticketType(ticketType)
                .attendee(attendee)
                .purchaseTime(LocalDateTime.now())
                .status(TicketStatus.ACTIVE)
                .ticketCode(UUID.randomUUID().toString()) // Generate unique ticket code
                .build();

        Ticket savedTicket = ticketRepository.save(newTicket);
        return ticketMapper.toTicketResponse(savedTicket);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TicketResponse> getMyTickets(Long attendeeAppUserId, Pageable pageable) {
        Page<Ticket> ticketsPage = ticketRepository.findByAttendeeId(attendeeAppUserId, pageable);
        return ticketsPage.map(ticketMapper::toTicketResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public TicketResponse getMyTicketById(Long ticketId, Long attendeeAppUserId) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket", "id", ticketId));
        if (!Objects.equals(ticket.getAttendee().getId(), attendeeAppUserId)) {
            throw new AccessDeniedException("You are not authorized to view this ticket.");
        }
        return ticketMapper.toTicketResponse(ticket);
    }

    @Override
    @Transactional(readOnly = true)
    public byte[] getTicketQrCode(Long ticketId, Long attendeeAppUserId) throws Exception {
        TicketResponse ticketResponse = getMyTicketById(ticketId, attendeeAppUserId); // Performs ownership check

        String qrContent = ticketResponse.getTicketCode(); // Using only ticket code for QR simplicity
        // Alternatively, build a JSON string with more details:
        // String qrContent = String.format("{\"ticketCode\":\"%s\",\"eventId\":%d,\"eventName\":\"%s\"}",
        //    ticketResponse.getTicketCode(), ticketResponse.getEventId(), eventName); // eventName needs to be fetched or added to TicketResponse

        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(qrContent, BarcodeFormat.QR_CODE, 256, 256); // width, height

        ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(bitMatrix, "PNG", pngOutputStream);
        return pngOutputStream.toByteArray();
    }

    // Staff specific methods
    @Override
    public TicketResponse validateTicket(Long eventId, String ticketCode, JwtAuthenticationToken staffPrincipal) {
        AppUser staffUser = appUserService.findOrCreateAppUserEntity(staffPrincipal);

        // Fetch the event - not strictly needed for validation if ticketCode is globally unique
        // and ticket contains eventId, but good for ensuring staff is acting on the correct event context.
        Event event = eventRepository.findById(eventId)
            .orElseThrow(() -> new ResourceNotFoundException("Event", "id", eventId));

        Ticket ticket = ticketRepository.findByTicketCode(ticketCode)
            .orElseThrow(() -> new ResourceNotFoundException("Ticket", "code", ticketCode));

        if (!Objects.equals(ticket.getEvent().getId(), eventId)) {
            throw new IllegalStateException("Ticket (code: " + ticketCode + ") does not belong to the specified event (ID: " + eventId + "). It belongs to event ID: " + ticket.getEvent().getId());
        }

        switch (ticket.getStatus()) {
            case VALIDATED:
                String validatedByInfo = "N/A";
                if (ticket.getValidatedBy() != null) {
                    // Assuming AppUser has a meaningful identifier like username or just ID
                    validatedByInfo = "Staff ID " + ticket.getValidatedBy().getId()
                                    + (ticket.getValidatedBy().getUsername() != null ? " (" + ticket.getValidatedBy().getUsername() + ")" : "");
                }
                throw new IllegalStateException("Ticket already validated on " + ticket.getValidationTime() + " by " + validatedByInfo + ".");
            case CANCELLED:
                throw new IllegalStateException("Ticket is cancelled and cannot be validated.");
            case ACTIVE:
                ticket.setStatus(TicketStatus.VALIDATED);
                ticket.setValidationTime(LocalDateTime.now());
                ticket.setValidatedBy(staffUser);
                ticketRepository.save(ticket);
                break;
            default:
                throw new IllegalStateException("Ticket is in an unknown state and cannot be validated.");
        }
        return ticketMapper.toTicketResponse(ticket);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TicketResponse> getValidatedTicketsForEvent(Long eventId, JwtAuthenticationToken staffPrincipal, Pageable pageable) {
        // Ensures staff user exists, could also be used for logging/auditing staff actions
        appUserService.findOrCreateAppUserEntity(staffPrincipal);

        // Ensure event exists before querying tickets for it
        if (!eventRepository.existsById(eventId)) {
            throw new ResourceNotFoundException("Event", "id", eventId);
        }

        Page<Ticket> validatedTicketsPage = ticketRepository.findByEventIdAndStatus(eventId, TicketStatus.VALIDATED, pageable);
        return validatedTicketsPage.map(ticketMapper::toTicketResponse);
    }
}
