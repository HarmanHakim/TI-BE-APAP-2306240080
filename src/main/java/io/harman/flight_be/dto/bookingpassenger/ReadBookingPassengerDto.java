package io.harman.flight_be.dto.bookingpassenger;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReadBookingPassengerDto {
    private String bookingId;
    private UUID passengerId;
    private String passengerName;
    private LocalDateTime createdAt;
}
