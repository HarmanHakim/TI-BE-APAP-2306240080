package io.harman.flight_be.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.annotations.SQLDelete;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "bookings")
@SQLDelete(sql = "UPDATE bookings SET is_deleted = true, status = 3, updated_at = NOW() WHERE id=?")
public class Booking {

    @Id
    @Column(name = "id", nullable = false)
    private String id;

    @Column(name = "flight_id", nullable = false)
    private String flightId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "flight_id", referencedColumnName = "id", insertable = false, updatable = false)
    private Flight flight;

    @Column(name = "class_flight_id", nullable = false)
    private Integer classFlightId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "class_flight_id", referencedColumnName = "id", insertable = false, updatable = false)
    private ClassFlight classFlight;

    @Column(name = "contact_email", nullable = false)
    private String contactEmail;

    @Column(name = "contact_phone", nullable = false)
    private String contactPhone;

    @Column(name = "passenger_count", nullable = false)
    private Integer passengerCount;

    @Column(name = "status", nullable = false)
    @Builder.Default
    private Integer status = 1; // 1=Unpaid, 2=Paid, 3=Cancelled, 4=Rescheduled

    @Column(name = "total_price", nullable = false, precision = 15, scale = 2)
    private BigDecimal totalPrice;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "booking_passengers", joinColumns = @JoinColumn(name = "booking_id"), inverseJoinColumns = @JoinColumn(name = "passenger_id"))
    private List<Passenger> passengers;

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
            status = 1; // Default: Unpaid
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
            status = 3;
        }
    }
}
