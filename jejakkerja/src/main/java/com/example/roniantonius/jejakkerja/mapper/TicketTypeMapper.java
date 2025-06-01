package com.example.roniantonius.jejakkerja.mapper;

import com.example.roniantonius.jejakkerja.domain.entity.TicketType;
import com.example.roniantonius.jejakkerja.dto.tickertype.CreateTicketTypeRequest;
import com.example.roniantonius.jejakkerja.dto.tickertype.TicketTypeResponse;
import com.example.roniantonius.jejakkerja.dto.tickertype.UpdateTicketTypeRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TicketTypeMapper {

    @Mapping(source = "event.id", target = "eventId")
    TicketTypeResponse toTicketTypeResponse(TicketType ticketType);

    TicketType toTicketType(CreateTicketTypeRequest request);

    void updateTicketTypeFromRequest(UpdateTicketTypeRequest request, @MappingTarget TicketType ticketType);

    List<TicketTypeResponse> toTicketTypeResponseList(List<TicketType> ticketTypes);
}
