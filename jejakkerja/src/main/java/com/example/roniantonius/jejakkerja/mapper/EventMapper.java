package com.example.roniantonius.jejakkerja.mapper;

import com.example.roniantonius.jejakkerja.domain.entity.Event;
import com.example.roniantonius.jejakkerja.dto.event.CreateEventRequest;
import com.example.roniantonius.jejakkerja.dto.event.EventResponse;
import com.example.roniantonius.jejakkerja.dto.event.UpdateEventRequest;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring", uses = {TicketTypeMapper.class})
public interface EventMapper {

    EventResponse toEventResponse(Event event);

    Event toEvent(CreateEventRequest request);

    void updateEventFromRequest(UpdateEventRequest request, @MappingTarget Event event);

    List<EventResponse> toEventResponseList(List<Event> events);
}
