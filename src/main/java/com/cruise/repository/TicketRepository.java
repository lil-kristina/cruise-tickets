package com.cruise.repository;

import com.cruise.entity.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Integer> {

    // Билеты конкретного пользователя
    List<Ticket> findByUser_Id(Integer userId);

    // Билеты на конкретный рейс
    List<Ticket> findByCruise_Id(Integer cruiseId);

    // Активные билеты пользователя
    List<Ticket> findByUser_IdAndStatus(Integer userId, String status);

    // Количество активных билетов (для статистики)
    @Query("SELECT COUNT(t) FROM Ticket t WHERE t.status = 'ACTIVE'")
    Long countActive();

    // Суммарная выручка по активным билетам
    @Query("SELECT COALESCE(SUM(t.price), 0) FROM Ticket t WHERE t.status = 'ACTIVE'")
    BigDecimal sumActiveRevenue();

    // Общее количество билетов
    @Query("SELECT COUNT(t) FROM Ticket t")
    Long countAll();
}
