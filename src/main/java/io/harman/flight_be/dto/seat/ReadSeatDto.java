package io.harman.flight_be.dto.seat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReadSeatDto {
    private Long id;
    private Integer classFlightId;
    private String classType;
    private String flightId;
    private String seatNumber;
    private Boolean isAvailable;
    private UUID passengerId;
    private String passengerName;
}
