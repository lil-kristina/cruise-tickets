package com.cruise.controller;

import com.cruise.dto.stats.OwnerSummaryResponse;
import com.cruise.service.StatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/stats")
@RequiredArgsConstructor
public class StatsController {

    private final StatsService statsService;

    // GET /api/stats/summary — сводная статистика (только OWNER)
    @GetMapping("/summary")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<OwnerSummaryResponse> getSummary() {
        return ResponseEntity.ok(statsService.getSummary());
    }
}