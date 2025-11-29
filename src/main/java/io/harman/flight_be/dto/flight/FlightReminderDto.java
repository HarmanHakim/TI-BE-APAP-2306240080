package io.harman.flight_be.dto.flight;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FlightReminderDto {
    private String flightNumber;
    private String airline;
    private String origin;
    private String destination;
    private LocalDateTime departureTime;
    private Long remainingMinutes; // Time until departure in minutes
    private String remainingTime; // Formatted time (e.g., "2h 30m")
    private Integer status;
    private String statusLabel;
    private Long totalPaidBookings;
    private Long totalUnpaidBookings;
}
