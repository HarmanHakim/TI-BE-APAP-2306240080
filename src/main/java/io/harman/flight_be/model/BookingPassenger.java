package io.harman.flight_be.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "booking_passengers")
@IdClass(BookingPassenger.BookingPassengerId.class)
public class BookingPassenger {

    @Id
    @Column(name = "booking_id", nullable = false)
    private String bookingId;

    @Id
    @Column(name = "passenger_id", nullable = false)
    private UUID passengerId;

    @Column(name = "created_at", nullable = false, insertable = false, updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BookingPassengerId implements Serializable {
        private String bookingId;
        private UUID passengerId;
    }
}
