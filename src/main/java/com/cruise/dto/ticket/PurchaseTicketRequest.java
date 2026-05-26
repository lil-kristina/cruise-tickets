package com.cruise.dto.ticket;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PurchaseTicketRequest {

    @NotNull(message = "ID круиза обязателен")
    private Integer cruiseId;

    @NotNull(message = "Класс билета обязателен")
    private Integer ticketClassId;
}
