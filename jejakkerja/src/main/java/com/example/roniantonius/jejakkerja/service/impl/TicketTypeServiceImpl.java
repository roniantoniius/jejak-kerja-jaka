package com.example.roniantonius.jejakkerja.service.impl;

import com.example.roniantonius.jejakkerja.domain.entity.Event;
import com.example.roniantonius.jejakkerja.domain.entity.TicketType;
import com.example.roniantonius.jejakkerja.domain.repository.EventRepository;
import com.example.roniantonius.jejakkerja.domain.repository.TicketTypeRepository;
import com.example.roniantonius.jejakkerja.dto.tickertype.CreateTicketTypeRequest;
import com.example.roniantonius.jejakkerja.dto.tickertype.TicketTypeResponse;
import com.example.roniantonius.jejakkerja.dto.tickertype.UpdateTicketTypeRequest;
import com.example.roniantonius.jejakkerja.exception.ResourceNotFoundException;
import com.example.roniantonius.jejakkerja.mapper.TicketTypeMapper;
import com.example.roniantonius.jejakkerja.service.TicketTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional
public class TicketTypeServiceImpl implements TicketTypeService {

    private final TicketTypeRepository ticketTypeRepository;
    private final TicketTypeMapper ticketTypeMapper;
    private final EventRepository eventRepository;

    private Event getEventIfOrganizer(Long eventId, String organizerId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event", "id", eventId));
        if (!Objects.equals(event.getOrganizerId(), organizerId)) {
            throw new AccessDeniedException("You are not authorized to manage ticket types for this event.");
        }
        return event;
    }

    @Override
    public TicketTypeResponse createTicketType(Long eventId, CreateTicketTypeRequest request, String organizerId) {
        Event event = getEventIfOrganizer(eventId, organizerId);
        TicketType ticketType = ticketTypeMapper.toTicketType(request);
        ticketType.setEvent(event);
        ticketType.setRemainingQuantity(request.getQuantity()); // Initialize remainingQuantity
        TicketType savedTicketType = ticketTypeRepository.save(ticketType);
        return ticketTypeMapper.toTicketTypeResponse(savedTicketType);
    }

    @Override
    @Transactional(readOnly = true)
    public TicketTypeResponse getTicketTypeById(Long eventId, Long ticketTypeId, String organizerId) {
        getEventIfOrganizer(eventId, organizerId); // Validates event ownership
        TicketType ticketType = ticketTypeRepository.findById(ticketTypeId)
                .orElseThrow(() -> new ResourceNotFoundException("TicketType", "id", ticketTypeId));
        if (!Objects.equals(ticketType.getEvent().getId(), eventId)) {
            throw new ResourceNotFoundException("TicketType", "id", ticketTypeId + " not associated with event " + eventId);
        }
        return ticketTypeMapper.toTicketTypeResponse(ticketType);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TicketTypeResponse> getAllTicketTypesByEvent(Long eventId, String organizerId) {
        getEventIfOrganizer(eventId, organizerId); // Validates event ownership
        List<TicketType> ticketTypes = ticketTypeRepository.findByEventId(eventId);
        return ticketTypeMapper.toTicketTypeResponseList(ticketTypes);
    }

    @Override
    public TicketTypeResponse updateTicketType(Long eventId, Long ticketTypeId, UpdateTicketTypeRequest request, String organizerId) {
        getEventIfOrganizer(eventId, organizerId); // Validates event ownership
        TicketType ticketType = ticketTypeRepository.findById(ticketTypeId)
                .orElseThrow(() -> new ResourceNotFoundException("TicketType", "id", ticketTypeId));

        if (!Objects.equals(ticketType.getEvent().getId(), eventId)) {
            throw new ResourceNotFoundException("TicketType", "id", ticketTypeId + " not associated with event " + eventId);
        }

        int soldTickets = 0;
        if(ticketType.getQuantity() != null && ticketType.getRemainingQuantity() != null) {
            soldTickets = ticketType.getQuantity() - ticketType.getRemainingQuantity();
        }

        ticketTypeMapper.updateTicketTypeFromRequest(request, ticketType); // Updates name, price, quantity

        if (request.getQuantity() != null) { // If quantity is part of the update request
            ticketType.setRemainingQuantity(request.getQuantity() - soldTickets);
            if (ticketType.getRemainingQuantity() < 0) {
                throw new IllegalStateException("New quantity ("+ request.getQuantity() +") cannot be less than already sold tickets (" + soldTickets + ").");
            }
        }
        // If quantity is not in UpdateTicketTypeRequest or not being updated, remainingQuantity logic might differ
        // For now, this assumes quantity in UpdateTicketTypeRequest is the new total quantity.

        TicketType updatedTicketType = ticketTypeRepository.save(ticketType);
        return ticketTypeMapper.toTicketTypeResponse(updatedTicketType);
    }

    @Override
    public void deleteTicketType(Long eventId, Long ticketTypeId, String organizerId) {
        getEventIfOrganizer(eventId, organizerId); // Validates event ownership
        TicketType ticketType = ticketTypeRepository.findById(ticketTypeId)
                .orElseThrow(() -> new ResourceNotFoundException("TicketType", "id", ticketTypeId));

        if (!Objects.equals(ticketType.getEvent().getId(), eventId)) {
            throw new ResourceNotFoundException("TicketType", "id", ticketTypeId + " not associated with event " + eventId);
        }

        // Future check: prevent deletion if tickets of this type have been sold.
        // For example, by checking if (ticketType.getQuantity() - ticketType.getRemainingQuantity()) > 0
        int soldTickets = 0;
        if(ticketType.getQuantity() != null && ticketType.getRemainingQuantity() != null) {
            soldTickets = ticketType.getQuantity() - ticketType.getRemainingQuantity();
        }
        if (soldTickets > 0) {
            throw new IllegalStateException("Cannot delete ticket type with ID " + ticketTypeId + " because " + soldTickets + " tickets have already been sold.");
        }

        ticketTypeRepository.delete(ticketType);
    }
}
