package com.cruise.service;

import com.cruise.dto.ticket.PurchaseTicketRequest;
import com.cruise.dto.ticket.TicketResponse;
import com.cruise.entity.Cruise;
import com.cruise.entity.Ticket;
import com.cruise.entity.TicketClass;
import com.cruise.entity.User;
import com.cruise.exception.ApiException;
import com.cruise.repository.CruiseRepository;
import com.cruise.repository.TicketClassRepository;
import com.cruise.repository.TicketRepository;
import com.cruise.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TicketService {

    private final TicketRepository ticketRepository;
    private final CruiseRepository cruiseRepository;
    private final TicketClassRepository ticketClassRepository;
    private final UserRepository userRepository;

    // Купить билет
    @Transactional
    public TicketResponse purchase(PurchaseTicketRequest request, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> ApiException.notFound("Пользователь не найден"));

        Cruise cruise = cruiseRepository.findById(request.getCruiseId())
                .orElseThrow(() -> ApiException.notFound("Круиз не найден"));

        // Проверка доступности
        if (!cruise.getIsActive()) {
            throw ApiException.badRequest("Этот рейс недоступен для бронирования");
        }
        if (cruise.getAvailableSeats() <= 0) {
            throw ApiException.badRequest("На этот рейс нет свободных мест");
        }

        TicketClass ticketClass = ticketClassRepository.findById(request.getTicketClassId())
                .orElseThrow(() -> ApiException.notFound("Класс билета не найден"));

        // Расчёт цены: базовая цена типа круиза × множитель класса
        BigDecimal price = cruise.getCruiseType().getBasePrice()
                .multiply(ticketClass.getPriceFactor());

        // Создаём билет
        Ticket ticket = new Ticket();
        ticket.setUser(user);
        ticket.setCruise(cruise);
        ticket.setTicketClass(ticketClass);
        ticket.setPrice(price);
        ticket.setStatus("ACTIVE");

        // Уменьшаем количество свободных мест
        cruise.setAvailableSeats(cruise.getAvailableSeats() - 1);
        cruiseRepository.save(cruise);

        return toResponse(ticketRepository.save(ticket));
    }

    // Отменить билет
    @Transactional
    public TicketResponse cancel(Integer ticketId, String userEmail, String role) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> ApiException.notFound("Билет с id=" + ticketId + " не найден"));

        // USER может отменять только свои билеты
        if (role.equals("USER") && !ticket.getUser().getEmail().equals(userEmail)) {
            throw ApiException.forbidden("Вы можете отменять только свои билеты");
        }

        if (ticket.getStatus().equals("CANCELLED")) {
            throw ApiException.badRequest("Билет уже отменён");
        }

        ticket.setStatus("CANCELLED");

        // Возвращаем место
        Cruise cruise = ticket.getCruise();
        cruise.setAvailableSeats(cruise.getAvailableSeats() + 1);
        cruiseRepository.save(cruise);

        return toResponse(ticketRepository.save(ticket));
    }

    // Мои билеты (для USER)
    public List<TicketResponse> getMyTickets(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> ApiException.notFound("Пользователь не найден"));
        return ticketRepository.findByUser_Id(user.getId())
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // Все билеты (для ADMIN/OWNER)
    public List<TicketResponse> getAll() {
        return ticketRepository.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // Маппинг entity → DTO
    public TicketResponse toResponse(Ticket ticket) {
        TicketResponse dto = new TicketResponse();
        dto.setId(ticket.getId());
        dto.setUserId(ticket.getUser().getId());
        dto.setUserName(ticket.getUser().getName());
        dto.setUserEmail(ticket.getUser().getEmail());
        dto.setCruiseType(ticket.getCruise().getCruiseType().getName());
        dto.setShipName(ticket.getCruise().getShip().getName());
        dto.setDepartureDate(ticket.getCruise().getDepartureDate());
        dto.setReturnDate(ticket.getCruise().getReturnDate());
        dto.setTicketClass(ticket.getTicketClass().getName());
        dto.setPrice(ticket.getPrice());
        dto.setStatus(ticket.getStatus());
        dto.setPurchaseDate(ticket.getPurchaseDate());
        return dto;
    }
}