package io.harman.flight_be.dto.booking;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReadBookingDto {
    private String id;
    private String flightId;
    private String flightNumber;
    private String originAirportCode;
    private String destinationAirportCode;
    private LocalDateTime departureTime;
    private LocalDateTime arrivalTime;
    private Integer classFlightId;
    private String classType;
    private String contactEmail;
    private String contactPhone;
    private Integer passengerCount;
    private Integer status; // 1=Unpaid, 2=Paid, 3=Cancelled, 4=Rescheduled
    private String statusLabel;
    private BigDecimal totalPrice;
    private List<PassengerSummary> passengers;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Boolean isDeleted;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PassengerSummary {
        private String id;
        private String fullName;
        private String seatNumber;
    }
}
