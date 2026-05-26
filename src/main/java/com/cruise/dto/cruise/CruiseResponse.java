package com.cruise.dto.cruise;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class CruiseResponse {
    private Integer id;
    private String cruiseType;
    private String description;
    private Integer durationDays;
    private String shipName;
    private LocalDate departureDate;
    private LocalDate returnDate;
    private Integer availableSeats;
    private BigDecimal basePrice;
    private String imageUrl;
    private Boolean isActive;
}
