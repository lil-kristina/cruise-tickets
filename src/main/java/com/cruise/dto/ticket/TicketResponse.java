package com.cruise.dto.ticket;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class TicketResponse {
    private Integer id;
    private Integer userId;
    private String userName;
    private String userEmail;
    private String cruiseType;
    private String shipName;
    private LocalDate departureDate;
    private LocalDate returnDate;
    private String ticketClass;      // ECONOMY / BUSINESS / LUXURY
    private BigDecimal price;        // итоговая цена с учётом класса
    private String status;           // ACTIVE / CANCELLED
    private LocalDateTime purchaseDate;
}
