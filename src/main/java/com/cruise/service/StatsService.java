package com.cruise.service;

import com.cruise.dto.stats.OwnerSummaryResponse;
import com.cruise.repository.CruiseRepository;
import com.cruise.repository.TicketRepository;
import com.cruise.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StatsService {

    private final UserRepository userRepository;
    private final CruiseRepository cruiseRepository;
    private final TicketRepository ticketRepository;

    public OwnerSummaryResponse getSummary() {
        OwnerSummaryResponse dto = new OwnerSummaryResponse();

        // Всего пользователей с ролью USER
        dto.setTotalUsers((long) userRepository.findByRole_Name("USER").size());

        // Активных рейсов
        dto.setTotalCruises((long) cruiseRepository.findByIsActiveTrue().size());

        // Активных билетов и выручка
        dto.setTotalTickets(ticketRepository.countActive());
        dto.setTotalRevenue(ticketRepository.sumActiveRevenue());

        return dto;
    }
}