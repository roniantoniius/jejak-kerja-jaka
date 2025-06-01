package com.example.roniantonius.jejakkerja.dto.tickertype;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TicketTypeResponse {
    private Long id;
    private String name;
    private BigDecimal price;
    private Integer quantity;
    private Integer remainingQuantity;
    private Long eventId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
