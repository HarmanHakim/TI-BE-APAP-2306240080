package io.harman.flight_be.model.flight;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "passengers")
public class Passenger {
    
    @Id
    @Column(name = "id", nullable = false)
    private UUID id; // UUID unik untuk penumpang
    
    @Column(name = "full_name", nullable = false)
    private String fullName;
    
    @Column(name = "birth_date", nullable = false)
    private LocalDate birthDate;
    
    @Column(name = "gender", nullable = false)
    private Integer gender; // 1 = Male, 2 = Female, 3 = Other
    
    @Column(name = "id_passport", nullable = false)
    private String idPassport; // Nomor identitas atau paspor
    
    @ManyToMany(mappedBy = "passengers", fetch = FetchType.LAZY)
    private List<Booking> bookings;
    
    @OneToMany(mappedBy = "passenger", fetch = FetchType.LAZY)
    private List<Seat> seatsList;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        if (id == null) {
            id = UUID.randomUUID();
        }
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
