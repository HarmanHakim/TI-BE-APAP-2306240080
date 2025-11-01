package io.harman.flight_be.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "class_flights")
public class ClassFlight {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    
    @Column(name = "flight_id", nullable = false)
    private String flightId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "flight_id", referencedColumnName = "id", insertable = false, updatable = false)
    private Flight flight;
    
    @Column(name = "class_type", nullable = false)
    private String classType; // Economy, Business, First
    
    @Column(name = "seat_capacity", nullable = false)
    private Integer seatCapacity; 
    
    @OneToMany(mappedBy = "classFlight", fetch = FetchType.LAZY)
    private List<Seat> classSeats;
    
    @Column(name = "available_seats", nullable = false)
    private Integer availableSeats; // Kursi yang masih tersedia
    
    @Column(name = "price", nullable = false, precision = 15, scale = 2)
    private BigDecimal price;
    
    @PrePersist
    protected void onCreate() {
        if (availableSeats == null && seatCapacity != null) {
            availableSeats = seatCapacity;
        }
    }
}
