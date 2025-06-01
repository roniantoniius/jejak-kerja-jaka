package com.example.roniantonius.jejakkerja.service;

import com.example.roniantonius.jejakkerja.dto.tickertype.CreateTicketTypeRequest;
import com.example.roniantonius.jejakkerja.dto.tickertype.TicketTypeResponse;
import com.example.roniantonius.jejakkerja.dto.tickertype.UpdateTicketTypeRequest;

import java.util.List;

public interface TicketTypeService {

    TicketTypeResponse createTicketType(Long eventId, CreateTicketTypeRequest request, String organizerId);

    TicketTypeResponse getTicketTypeById(Long eventId, Long ticketTypeId, String organizerId);

    List<TicketTypeResponse> getAllTicketTypesByEvent(Long eventId, String organizerId);

    TicketTypeResponse updateTicketType(Long eventId, Long ticketTypeId, UpdateTicketTypeRequest request, String organizerId);

    void deleteTicketType(Long eventId, Long ticketTypeId, String organizerId);
}
