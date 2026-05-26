package com.cruise.dto.stats;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class OwnerSummaryResponse {
    private Long totalUsers;      // всего пользователей с ролью USER
    private Long totalCruises;    // активных рейсов
    private Long totalTickets;    // купленных (активных) билетов
    private BigDecimal totalRevenue; // суммарная выручка
}
