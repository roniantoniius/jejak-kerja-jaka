package com.example.roniantonius.jejakkerja.service.impl;

import com.example.roniantonius.jejakkerja.domain.entity.AppUser;
import com.example.roniantonius.jejakkerja.domain.entity.Event;
import com.example.roniantonius.jejakkerja.domain.repository.AppUserRepository;
import com.example.roniantonius.jejakkerja.domain.repository.EventRepository;
import com.example.roniantonius.jejakkerja.dto.event.CreateEventRequest;
import com.example.roniantonius.jejakkerja.dto.event.EventResponse;
import com.example.roniantonius.jejakkerja.dto.event.UpdateEventRequest;
import com.example.roniantonius.jejakkerja.exception.ResourceNotFoundException;
import com.example.roniantonius.jejakkerja.mapper.EventMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventServiceImplTest {

    @Mock
    private EventRepository eventRepository;

    @Mock
    private EventMapper eventMapper;

    @Mock
    private AppUserRepository appUserRepository; // Mocked, though not directly used in all methods under test here

    @InjectMocks
    private EventServiceImpl eventService;

    private CreateEventRequest createEventRequest;
    private UpdateEventRequest updateEventRequest;
    private Event event;
    private EventResponse eventResponse;
    private String organizerId;
    private Long eventId;

    @BeforeEach
    void setUp() {
        organizerId = "organizer-123";
        eventId = 1L;

        createEventRequest = CreateEventRequest.builder()
                .name("Test Event")
                .description("Test Description")
                .startTime(LocalDateTime.now().plusDays(1))
                .endTime(LocalDateTime.now().plusDays(2))
                .venue("Test Venue")
                .build();

        updateEventRequest = UpdateEventRequest.builder()
                .name("Updated Event")
                .description("Updated Description")
                .isPublished(true)
                .build();

        event = Event.builder()
                .id(eventId)
                .name("Test Event")
                .description("Test Description")
                .startTime(createEventRequest.getStartTime())
                .endTime(createEventRequest.getEndTime())
                .venue(createEventRequest.getVenue())
                .organizerId(organizerId)
                .isPublished(false)
                .build();

        eventResponse = EventResponse.builder()
                .id(eventId)
                .name("Test Event")
                .description("Test Description")
                .organizerId(organizerId)
                .isPublished(false)
                .build();
    }

    // --- createEvent Tests ---
    @Test
    void testCreateEvent_Success() {
        when(eventMapper.toEvent(any(CreateEventRequest.class))).thenReturn(event);
        when(eventRepository.save(any(Event.class))).thenReturn(event); // ID would be set by DB
        when(eventMapper.toEventResponse(any(Event.class))).thenReturn(eventResponse);

        EventResponse result = eventService.createEvent(createEventRequest, organizerId);

        assertNotNull(result);
        assertEquals(eventResponse.getName(), result.getName());
        assertEquals(organizerId, event.getOrganizerId()); // Check event entity directly before save mock
        assertFalse(event.isPublished()); // Check event entity directly

        verify(eventMapper).toEvent(createEventRequest);
        verify(eventRepository).save(event);
        verify(eventMapper).toEventResponse(event);
    }

    // --- getAllEventsByOrganizer Tests ---
    @Test
    void testGetAllEventsByOrganizer_HasEvents() {
        Pageable pageable = PageRequest.of(0, 10);
        List<Event> events = Collections.singletonList(event);
        Page<Event> eventPage = new PageImpl<>(events, pageable, events.size()); // Corrected for EventRepository change

        // Assuming EventRepository.findByOrganizerId now returns Page<Event>
        // If it returns List<Event>, adjust this mock and the service method signature.
        // For this test, let's assume it's List for simplicity if Page isn't strictly tested here.
        // However, the current EventRepository returns List not Page for findByOrganizerId.
        // Let's stick to the current implementation of EventRepository for this test.
        when(eventRepository.findByOrganizerId(organizerId, pageable)).thenReturn(events);
        when(eventMapper.toEventResponseList(events)).thenReturn(Collections.singletonList(eventResponse));

        List<EventResponse> results = eventService.getAllEventsByOrganizer(organizerId, pageable);

        assertNotNull(results);
        assertFalse(results.isEmpty());
        assertEquals(1, results.size());
        assertEquals(eventResponse.getName(), results.get(0).getName());

        verify(eventRepository).findByOrganizerId(organizerId, pageable);
        verify(eventMapper).toEventResponseList(events);
    }

    @Test
    void testGetAllEventsByOrganizer_NoEvents() {
        Pageable pageable = PageRequest.of(0, 10);
        when(eventRepository.findByOrganizerId(organizerId, pageable)).thenReturn(Collections.emptyList());
        when(eventMapper.toEventResponseList(Collections.emptyList())).thenReturn(Collections.emptyList());

        List<EventResponse> results = eventService.getAllEventsByOrganizer(organizerId, pageable);

        assertNotNull(results);
        assertTrue(results.isEmpty());

        verify(eventRepository).findByOrganizerId(organizerId, pageable);
        verify(eventMapper).toEventResponseList(Collections.emptyList());
    }


    // --- getEventById Tests ---
    @Test
    void testGetEventById_Exists() {
        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));
        when(eventMapper.toEventResponse(event)).thenReturn(eventResponse);

        EventResponse result = eventService.getEventById(eventId);

        assertNotNull(result);
        assertEquals(eventResponse.getId(), result.getId());
        verify(eventRepository).findById(eventId);
        verify(eventMapper).toEventResponse(event);
    }

    @Test
    void testGetEventById_NotFound_ShouldThrowResourceNotFoundException() {
        when(eventRepository.findById(eventId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            eventService.getEventById(eventId);
        });
        verify(eventRepository).findById(eventId);
        verify(eventMapper, never()).toEventResponse(any());
    }

    // --- updateEvent Tests ---
    @Test
    void testUpdateEvent_Success() {
        Event existingEvent = Event.builder().id(eventId).name("Old Name").organizerId(organizerId).isPublished(false).build();
        Event updatedEvent = Event.builder().id(eventId).name(updateEventRequest.getName()).organizerId(organizerId).isPublished(true).build();
        EventResponse updatedResponse = EventResponse.builder().id(eventId).name(updateEventRequest.getName()).isPublished(true).build();

        when(eventRepository.findById(eventId)).thenReturn(Optional.of(existingEvent));
        // updateEventFromRequest is void, it modifies existingEvent
        doNothing().when(eventMapper).updateEventFromRequest(any(UpdateEventRequest.class), any(Event.class));
        when(eventRepository.save(any(Event.class))).thenReturn(updatedEvent); // Assume save returns the state after update
        when(eventMapper.toEventResponse(any(Event.class))).thenReturn(updatedResponse);


        EventResponse result = eventService.updateEvent(eventId, updateEventRequest, organizerId);

        assertNotNull(result);
        assertEquals(updateEventRequest.getName(), result.getName());
        assertTrue(result.isPublished());

        verify(eventRepository).findById(eventId);
        verify(eventMapper).updateEventFromRequest(updateEventRequest, existingEvent);
        verify(eventRepository).save(existingEvent); // existingEvent is modified and saved
        verify(eventMapper).toEventResponse(updatedEvent);
    }

    @Test
    void testUpdateEvent_NotFound_ShouldThrowResourceNotFoundException() {
        when(eventRepository.findById(eventId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            eventService.updateEvent(eventId, updateEventRequest, organizerId);
        });
        verify(eventRepository).findById(eventId);
        verify(eventMapper, never()).updateEventFromRequest(any(), any());
        verify(eventRepository, never()).save(any());
    }

    @Test
    void testUpdateEvent_AccessDenied_ShouldThrowAccessDeniedException() {
        Event existingEvent = Event.builder().id(eventId).organizerId("other-organizer").build();
        when(eventRepository.findById(eventId)).thenReturn(Optional.of(existingEvent));

        assertThrows(AccessDeniedException.class, () -> {
            eventService.updateEvent(eventId, updateEventRequest, organizerId);
        });
        verify(eventRepository).findById(eventId);
        verify(eventMapper, never()).updateEventFromRequest(any(), any());
        verify(eventRepository, never()).save(any());
    }


    // --- deleteEvent Tests ---
    @Test
    void testDeleteEvent_Success() {
        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event)); // event has correct organizerId from setUp

        eventService.deleteEvent(eventId, organizerId);

        verify(eventRepository).findById(eventId);
        verify(eventRepository).delete(event);
    }

    @Test
    void testDeleteEvent_NotFound_ShouldThrowResourceNotFoundException() {
        when(eventRepository.findById(eventId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            eventService.deleteEvent(eventId, organizerId);
        });
        verify(eventRepository).findById(eventId);
        verify(eventRepository, never()).delete(any());
    }

    @Test
    void testDeleteEvent_AccessDenied_ShouldThrowAccessDeniedException() {
        Event existingEvent = Event.builder().id(eventId).organizerId("other-organizer").build();
        when(eventRepository.findById(eventId)).thenReturn(Optional.of(existingEvent));

        assertThrows(AccessDeniedException.class, () -> {
            eventService.deleteEvent(eventId, organizerId);
        });
        verify(eventRepository).findById(eventId);
        verify(eventRepository, never()).delete(any());
    }

    // --- getAllPublishedEvents Tests ---
    @Test
    void testGetAllPublishedEvents_HasEvents() {
        Pageable pageable = PageRequest.of(0, 10);
        Event publishedEvent = Event.builder().id(2L).name("Published Event").isPublished(true).build();
        List<Event> events = Collections.singletonList(publishedEvent);
        // EventRepository.findByIsPublishedTrue returns List<Event>
        when(eventRepository.findByIsPublishedTrue(pageable)).thenReturn(events);

        EventResponse publishedEventResponse = EventResponse.builder().id(2L).name("Published Event").isPublished(true).build();
        when(eventMapper.toEventResponseList(events)).thenReturn(Collections.singletonList(publishedEventResponse));

        List<EventResponse> results = eventService.getAllPublishedEvents(pageable);

        assertNotNull(results);
        assertFalse(results.isEmpty());
        assertEquals(1, results.size());
        assertTrue(results.get(0).isPublished());

        verify(eventRepository).findByIsPublishedTrue(pageable);
        verify(eventMapper).toEventResponseList(events);
    }

    // --- getPublishedEventById Tests ---
    @Test
    void testGetPublishedEventById_ExistsAndPublished() {
        event.setPublished(true); // Ensure the event is published for this test
        eventResponse.setPublished(true);
        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));
        when(eventMapper.toEventResponse(event)).thenReturn(eventResponse);

        EventResponse result = eventService.getPublishedEventById(eventId);

        assertNotNull(result);
        assertTrue(result.isPublished());
        verify(eventRepository).findById(eventId);
        verify(eventMapper).toEventResponse(event);
    }

    @Test
    void testGetPublishedEventById_ExistsButNotPublished_ShouldThrowResourceNotFoundException() {
        event.setPublished(false); // Ensure event is not published
        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));
        // No need to mock eventMapper.toEventResponse as it shouldn't be called

        assertThrows(ResourceNotFoundException.class, () -> {
            eventService.getPublishedEventById(eventId);
        }, "Event with id " + eventId + " (not published)");
        verify(eventRepository).findById(eventId);
        verify(eventMapper, never()).toEventResponse(any());
    }

    @Test
    void testGetPublishedEventById_NotFound_ShouldThrowResourceNotFoundException() {
        when(eventRepository.findById(eventId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            eventService.getPublishedEventById(eventId);
        });
        verify(eventRepository).findById(eventId);
        verify(eventMapper, never()).toEventResponse(any());
    }
}
