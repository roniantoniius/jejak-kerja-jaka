package com.example.roniantonius.jejakkerja.dto.event;

import com.example.roniantonius.jejakkerja.dto.tickertype.TicketTypeResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventResponse {
    private Long id;
    private String name;
    private String description;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String venue;
    private String organizerId;
    private boolean isPublished;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<TicketTypeResponse> ticketTypes;
}
