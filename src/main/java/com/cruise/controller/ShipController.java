package com.cruise.controller;

import com.cruise.entity.Ship;
import com.cruise.repository.ShipRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/ships")
public class ShipController {

    @Autowired
    private ShipRepository shipRepository;

    @GetMapping
    public ResponseEntity<List<Ship>> getAll() {
        return ResponseEntity.ok(shipRepository.findAll());
    }
}