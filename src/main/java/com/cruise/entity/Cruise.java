package com.cruise.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@Entity
@Table(name = "cruises")
public class Cruise {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "cruise_type_id", nullable = false)
    private CruiseType cruiseType;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ship_id", nullable = false)
    private Ship ship;

    @Column(name = "departure_date", nullable = false)
    private LocalDate departureDate;

    @Column(name = "return_date", nullable = false)
    private LocalDate returnDate;

    @Column(name = "available_seats", nullable = false)
    private Integer availableSeats;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;
}
