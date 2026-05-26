package com.cruise.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Entity
@Table(name = "tickets")
public class Ticket {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "cruise_id", nullable = false)
    private Cruise cruise;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ticket_class_id", nullable = false)
    private TicketClass ticketClass;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(nullable = false, length = 20)
    private String status = "ACTIVE";

    @Column(name = "purchase_date")
    private LocalDateTime purchaseDate = LocalDateTime.now();
}
