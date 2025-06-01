package com.example.roniantonius.jejakkerja.dto.ticket;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ValidateTicketRequest {
    @NotBlank(message = "Ticket code cannot be blank")
    private String ticketCode;
}
