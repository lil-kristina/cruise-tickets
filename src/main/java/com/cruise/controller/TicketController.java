package com.cruise.controller;

import com.cruise.dto.ticket.PurchaseTicketRequest;
import com.cruise.dto.ticket.TicketResponse;
import com.cruise.service.TicketService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tickets")
@RequiredArgsConstructor
public class TicketController {

    private final TicketService ticketService;

    // GET /api/tickets/my — мои билеты (любой авторизованный)
    @GetMapping("/my")
    public ResponseEntity<List<TicketResponse>> getMyTickets(Authentication auth) {
        return ResponseEntity.ok(ticketService.getMyTickets(auth.getName()));
    }

    // GET /api/tickets — все билеты (ADMIN, OWNER)
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','OWNER')")
    public ResponseEntity<List<TicketResponse>> getAll() {
        return ResponseEntity.ok(ticketService.getAll());
    }

    // POST /api/tickets — купить билет (любой авторизованный)
    @PostMapping
    public ResponseEntity<TicketResponse> purchase(@Valid @RequestBody PurchaseTicketRequest request,
                                                   Authentication auth) {
        return ResponseEntity.ok(ticketService.purchase(request, auth.getName()));
    }

    // PUT /api/tickets/:id/cancel — отменить билет
    // USER — только свой, ADMIN/OWNER — любой
    @PutMapping("/{id}/cancel")
    public ResponseEntity<TicketResponse> cancel(@PathVariable Integer id,
                                                 Authentication auth) {
        // Получаем роль из Spring Security
        String role = auth.getAuthorities().iterator().next()
                .getAuthority().replace("ROLE_", "");
        return ResponseEntity.ok(ticketService.cancel(id, auth.getName(), role));
    }
}