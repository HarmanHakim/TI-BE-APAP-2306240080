package io.harman.flight_be.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "seats")
public class Seat {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    
    @Column(name = "class_flight_id", nullable = false)
    private Integer classFlightId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "class_flight_id", referencedColumnName = "id", insertable = false, updatable = false)
    private ClassFlight classFlight;
    
    @Column(name = "seat_number", nullable = false)
    private String seatNumber; // Contoh: 1A, 12B, 23C
    
    @Column(name = "is_available", nullable = false)
    @Builder.Default
    private Boolean isAvailable = true;
    
    @Column(name = "passenger_id")
    private UUID passengerId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "passenger_id", referencedColumnName = "id", insertable = false, updatable = false)
    private Passenger passenger;
    
    @PrePersist
    protected void onCreate() {
        if (isAvailable == null) {
            isAvailable = true;
        }
    }
}
