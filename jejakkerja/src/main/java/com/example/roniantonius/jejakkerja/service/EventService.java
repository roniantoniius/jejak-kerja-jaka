package com.example.roniantonius.jejakkerja.service;

import com.example.roniantonius.jejakkerja.dto.event.CreateEventRequest;
import com.example.roniantonius.jejakkerja.dto.event.EventResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface EventService {

    EventResponse createEvent(CreateEventRequest request, String organizerId);

    List<EventResponse> getAllEventsByOrganizer(String organizerId, Pageable pageable);

    List<EventResponse> getAllPublishedEvents(Pageable pageable);

    EventResponse getEventById(Long id);

    EventResponse getPublishedEventById(Long id);

    EventResponse updateEvent(Long id, com.example.roniantonius.jejakkerja.dto.event.UpdateEventRequest request, String organizerId);

    void deleteEvent(Long id, String organizerId);
}
