package com.cruise.repository;

import com.cruise.entity.CruiseType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CruiseTypeRepository extends JpaRepository<CruiseType, Integer> {
}