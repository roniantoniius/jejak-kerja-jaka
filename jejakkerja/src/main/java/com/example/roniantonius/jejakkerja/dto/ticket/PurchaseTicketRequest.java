package com.example.roniantonius.jejakkerja.dto.ticket;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseTicketRequest {
    private Long ticketTypeId;
    private Integer quantity; // Assuming a user might want to purchase more than one ticket of the same type
}
