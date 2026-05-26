package com.cruise.dto.cruise;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDate;

@Data
public class CreateCruiseRequest {

    @NotNull(message = "Тип круиза обязателен")
    private Integer cruiseTypeId;

    @NotNull(message = "Корабль обязателен")
    private Integer shipId;

    @NotNull(message = "Дата отправления обязательна")
    private LocalDate departureDate;

    @NotNull(message = "Дата возвращения обязательна")
    private LocalDate returnDate;

    @NotNull(message = "Количество мест обязательно")
    @Min(value = 1, message = "Мест должно быть минимум 1")
    private Integer availableSeats;
}
