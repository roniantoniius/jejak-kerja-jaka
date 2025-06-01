package com.example.roniantonius.jejakkerja.service.impl;

import com.example.roniantonius.jejakkerja.domain.entity.Event;
import com.example.roniantonius.jejakkerja.domain.repository.AppUserRepository;
import com.example.roniantonius.jejakkerja.domain.repository.EventRepository;
import com.example.roniantonius.jejakkerja.dto.event.CreateEventRequest;
import com.example.roniantonius.jejakkerja.dto.event.EventResponse;
import com.example.roniantonius.jejakkerja.dto.event.UpdateEventRequest;
import com.example.roniantonius.jejakkerja.exception.ResourceNotFoundException;
import com.example.roniantonius.jejakkerja.mapper.EventMapper;
import com.example.roniantonius.jejakkerja.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final EventMapper eventMapper;
    // AppUserRepository might be needed later for organizer validation, keeping it for now
    private final AppUserRepository appUserRepository;

    @Override
    public EventResponse createEvent(CreateEventRequest request, String organizerId) {
        // Optional: Validate if organizerId (from token) exists in AppUser table
        // appUserRepository.findByKeycloakId(organizerId)
        // .orElseThrow(() -> new ResourceNotFoundException("AppUser", "keycloakId", organizerId));

        Event event = eventMapper.toEvent(request);
        event.setOrganizerId(organizerId);
        event.setPublished(false); // Default to not published
        Event savedEvent = eventRepository.save(event);
        return eventMapper.toEventResponse(savedEvent);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventResponse> getAllEventsByOrganizer(String organizerId, Pageable pageable) {
        List<Event> events = eventRepository.findByOrganizerId(organizerId, pageable);
        return eventMapper.toEventResponseList(events);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventResponse> getAllPublishedEvents(Pageable pageable) {
        List<Event> events = eventRepository.findByIsPublishedTrue(pageable);
        return eventMapper.toEventResponseList(events);
    }

    @Override
    @Transactional(readOnly = true)
    public EventResponse getEventById(Long id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event", "id", id));
        return eventMapper.toEventResponse(event);
    }

    @Override
    @Transactional(readOnly = true)
    public EventResponse getPublishedEventById(Long id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event", "id", id));
        if (!event.isPublished()) {
            throw new ResourceNotFoundException("Event", "id", id + " (not published)");
        }
        return eventMapper.toEventResponse(event);
    }

    @Override
    public EventResponse updateEvent(Long id, UpdateEventRequest request, String organizerId) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event", "id", id));

        if (!Objects.equals(event.getOrganizerId(), organizerId)) {
            throw new AccessDeniedException("You are not authorized to update this event.");
        }

        eventMapper.updateEventFromRequest(request, event);
        Event updatedEvent = eventRepository.save(event);
        return eventMapper.toEventResponse(updatedEvent);
    }

    @Override
    public void deleteEvent(Long id, String organizerId) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event", "id", id));

        if (!Objects.equals(event.getOrganizerId(), organizerId)) {
            throw new AccessDeniedException("You are not authorized to delete this event.");
        }

        // Note: Cascading deletes for related entities (TicketTypes, Tickets)
        // depend on JPA annotations on the Event entity's relationships.
        // If not set to CascadeType.ALL or similar, this might fail or leave orphans.
        eventRepository.delete(event);
    }
}
