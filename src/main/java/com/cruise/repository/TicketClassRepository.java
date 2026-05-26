package com.cruise.repository;

import com.cruise.entity.TicketClass;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TicketClassRepository extends JpaRepository<TicketClass, Integer> {
    Optional<TicketClass> findByName(String name);
}
