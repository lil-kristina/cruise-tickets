package com.cruise.repository;

import com.cruise.entity.Cruise;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CruiseRepository extends JpaRepository<Cruise, Integer> {

    // Все доступные круизы (активные, есть места, дата в будущем)
    @Query("SELECT c FROM Cruise c WHERE c.isActive = true AND c.availableSeats > 0 AND c.departureDate >= CURRENT_DATE")
    List<Cruise> findAllAvailable();

    // Круизы по типу
    List<Cruise> findByCruiseType_Id(Integer cruiseTypeId);

    // Все активные (для админа)
    List<Cruise> findByIsActiveTrue();
}