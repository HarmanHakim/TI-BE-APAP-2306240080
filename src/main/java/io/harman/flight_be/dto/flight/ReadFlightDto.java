package io.harman.flight_be.dto.flight;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReadFlightDto {
    private String id;
    private String airlineId;
    private String airlineName;
    private String airlineCountry;
    private String airplaneId;
    private String airplaneModel;
    private String originAirportCode;
    private String destinationAirportCode;
    private LocalDateTime departureTime;
    private LocalDateTime arrivalTime;
    private String terminal;
    private String gate;
    private Integer baggageAllowance;
    private String facilities;
    private Integer status; // 1=Scheduled, 2=In Flight, 3=Finished, 4=Delayed, 5=Cancelled
    private String statusLabel;
    private List<ClassFlightSummary> classes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Boolean isDeleted;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ClassFlightSummary {
        private Integer id;
        private String classType;
        private Integer seatCapacity;
        private Integer availableSeats;
    }
}
