package com.cruise.controller;

import com.cruise.entity.CruiseType;
import com.cruise.service.CruiseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cruise-types")
@RequiredArgsConstructor
public class CruiseTypeController {

    private final CruiseService cruiseService;

    // GET /api/cruise-types — все типы круизов (публично)
    @GetMapping
    public ResponseEntity<List<CruiseType>> getAll() {
        return ResponseEntity.ok(cruiseService.getAllTypes());
    }
}