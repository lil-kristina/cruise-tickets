package com.cruise.controller;

import com.cruise.dto.cruise.CreateCruiseRequest;
import com.cruise.dto.cruise.CruiseResponse;
import com.cruise.service.CruiseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cruises")
@RequiredArgsConstructor
public class CruiseController {

    private final CruiseService cruiseService;

    // GET /api/cruises — доступные круизы (публично)
    @GetMapping
    public ResponseEntity<List<CruiseResponse>> getAvailable() {
        return ResponseEntity.ok(cruiseService.getAllAvailable());
    }

    // GET /api/cruises/all — все круизы включая неактивные (ADMIN, OWNER)
    @GetMapping("/all")
    @PreAuthorize("hasAnyRole('ADMIN','OWNER')")
    public ResponseEntity<List<CruiseResponse>> getAll() {
        return ResponseEntity.ok(cruiseService.getAll());
    }

    // GET /api/cruises/:id — круиз по id (публично)
    @GetMapping("/{id}")
    public ResponseEntity<CruiseResponse> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(cruiseService.getById(id));
    }

    // POST /api/cruises — создать рейс (ADMIN, OWNER)
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','OWNER')")
    public ResponseEntity<CruiseResponse> create(@Valid @RequestBody CreateCruiseRequest request) {
        return ResponseEntity.ok(cruiseService.create(request));
    }

    // PUT /api/cruises/:id — обновить рейс (ADMIN, OWNER)
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','OWNER')")
    public ResponseEntity<CruiseResponse> update(@PathVariable Integer id,
                                                 @Valid @RequestBody CreateCruiseRequest request) {
        return ResponseEntity.ok(cruiseService.update(id, request));
    }

    // DELETE /api/cruises/:id — деактивировать рейс (OWNER)
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<Void> deactivate(@PathVariable Integer id) {
        cruiseService.deactivate(id);
        return ResponseEntity.noContent().build();
    }
}