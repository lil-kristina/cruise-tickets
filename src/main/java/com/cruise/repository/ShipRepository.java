package com.cruise.repository;

import com.cruise.entity.Ship;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShipRepository extends JpaRepository<Ship, Integer> {
    List<Ship> findByOwner_Id(Integer ownerId);
}
