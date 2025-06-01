package com.example.roniantonius.jejakkerja.dto.tickertype;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateTicketTypeRequest {
    private String name;
    private BigDecimal price;
    private Integer quantity;
}
