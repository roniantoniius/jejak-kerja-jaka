package com.example.roniantonius.jejakkerja.controller;

import com.example.roniantonius.jejakkerja.dto.event.CreateEventRequest;
import com.example.roniantonius.jejakkerja.dto.event.EventResponse;
import com.example.roniantonius.jejakkerja.dto.event.UpdateEventRequest;
import com.example.roniantonius.jejakkerja.service.EventService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api/v1/events")
@RequiredArgsConstructor
@Validated
public class EventController {

    private final EventService eventService;

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_ORGANIZER')")
    public ResponseEntity<EventResponse> createEvent(@Valid @RequestBody CreateEventRequest request,
                                                     JwtAuthenticationToken principal) {
        String organizerId = principal.getToken().getSubject();
        EventResponse eventResponse = eventService.createEvent(request, organizerId);
        return new ResponseEntity<>(eventResponse, HttpStatus.CREATED);
    }

    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_ORGANIZER')")
    public ResponseEntity<List<EventResponse>> getOrganizerEvents(JwtAuthenticationToken principal,
                                                                  @ParameterObject Pageable pageable) {
        String organizerId = principal.getToken().getSubject();
        List<EventResponse> events = eventService.getAllEventsByOrganizer(organizerId, pageable);
        return ResponseEntity.ok(events);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ORGANIZER')")
    public ResponseEntity<EventResponse> getEventById(@PathVariable Long id,
                                                      JwtAuthenticationToken principal) {
        EventResponse eventResponse = eventService.getEventById(id);
        String organizerIdFromToken = principal.getToken().getSubject();
        if (!Objects.equals(eventResponse.getOrganizerId(), organizerIdFromToken)) {
            // Although PreAuthorize checks the role, this verifies ownership of the specific event.
            // Consider moving this logic into the service layer for cleaner controllers.
            // e.g., eventService.getEventByIdForOrganizer(id, organizerIdFromToken)
            throw new AccessDeniedException("You are not authorized to view this event.");
        }
        return ResponseEntity.ok(eventResponse);
    }

    // Public endpoint for attendees to view published events (example)
    @GetMapping("/published")
    public ResponseEntity<List<EventResponse>> getPublishedEvents(@ParameterObject Pageable pageable) {
        List<EventResponse> events = eventService.getAllPublishedEvents(pageable);
        return ResponseEntity.ok(events);
    }

    // Public endpoint for attendees to view a specific published event (example)
    @GetMapping("/published/{id}")
    public ResponseEntity<EventResponse> getPublishedEventById(@PathVariable Long id) {
        EventResponse eventResponse = eventService.getPublishedEventById(id);
        return ResponseEntity.ok(eventResponse);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ORGANIZER')")
    public ResponseEntity<EventResponse> updateEvent(@PathVariable Long id,
                                                     @Valid @RequestBody UpdateEventRequest request,
                                                     JwtAuthenticationToken principal) {
        String organizerId = principal.getToken().getSubject();
        EventResponse updatedEvent = eventService.updateEvent(id, request, organizerId);
        return ResponseEntity.ok(updatedEvent);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ORGANIZER')")
    public ResponseEntity<Void> deleteEvent(@PathVariable Long id,
                                            JwtAuthenticationToken principal) {
        String organizerId = principal.getToken().getSubject();
        eventService.deleteEvent(id, organizerId);
        return ResponseEntity.noContent().build();
    }
}
