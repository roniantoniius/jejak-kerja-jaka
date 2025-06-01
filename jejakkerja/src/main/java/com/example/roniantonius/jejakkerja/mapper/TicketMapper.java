package com.example.roniantonius.jejakkerja.mapper;

import com.example.roniantonius.jejakkerja.domain.entity.Ticket;
import com.example.roniantonius.jejakkerja.dto.ticket.TicketResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TicketMapper {

    @Mapping(source = "event.id", target = "eventId")
    @Mapping(source = "ticketType.id", target = "ticketTypeId")
    @Mapping(source = "attendee.id", target = "attendeeId")
    @Mapping(source = "validatedBy.id", target = "validatedByStaffId")
    TicketResponse toTicketResponse(Ticket ticket);

    List<TicketResponse> toTicketResponseList(List<Ticket> tickets);
}
