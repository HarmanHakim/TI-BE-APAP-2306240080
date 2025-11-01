package io.harman.flight_be.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "flights")
@SQLDelete(sql = "UPDATE flights SET is_deleted = true, status = 5, updated_at = NOW() WHERE id=?")
@Where(clause = "is_deleted = false")
public class Flight {
    
    @Id
    @Column(name = "id", nullable = false)
    private String id; 
    
    @Column(name = "airline_id", nullable = false, length = 3)
    private String airlineId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "airline_id", referencedColumnName = "id", insertable = false, updatable = false)
    private Airline airline;
    
    @Column(name = "airplane_id", nullable = false)
    private String airplaneId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "airplane_id", referencedColumnName = "id", insertable = false, updatable = false)
    private Airplane airplane;
    
    @Column(name = "origin_airport_code", nullable = false)
    private String originAirportCode; 
    
    @Column(name = "destination_airport_code", nullable = false)
    private String destinationAirportCode; 
    
    @Column(name = "departure_time", nullable = false)
    private LocalDateTime departureTime;
    
    @Column(name = "arrival_time", nullable = false)
    private LocalDateTime arrivalTime;
    
    @Column(name = "terminal")
    private String terminal;
    
    @Column(name = "gate")
    private String gate;
    
    @Column(name = "baggage_allowance")
    private Integer baggageAllowance; // Batas bagasi (kg)
    
    @Column(name = "facilities", columnDefinition = "TEXT")
    private String facilities; // Fasilitas tambahan (WiFi, meal, dll)
    
    @Column(name = "status", nullable = false)
    @Builder.Default
    private Integer status = 1; // 1=Scheduled, 2=In Flight, 3=Finished, 4=Delayed, 5=Cancelled
    
    @OneToMany(mappedBy = "flight", fetch = FetchType.LAZY)
    private List<ClassFlight> classes;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @Column(name = "is_deleted", nullable = false)
    @Builder.Default
    private Boolean isDeleted = false;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (status == null) {
            status = 1; // Default: Scheduled
        }
        if (isDeleted == null) {
            isDeleted = false;
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
        // Jika isDeleted = true, maka status harus Cancelled
        if (Boolean.TRUE.equals(isDeleted)) {
            status = 5;
        }
    }
}
