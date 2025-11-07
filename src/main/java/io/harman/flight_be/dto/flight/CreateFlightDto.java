package io.harman.flight_be.dto.flight;

import java.time.LocalDateTime;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateFlightDto {
    private String id;

    @NotBlank(message = "Airline ID is required")
    @Size(max = 3, min = 3, message = "Airline ID must be exactly 3 characters")
    private String airlineId;

    @NotBlank(message = "Airplane ID is required")
    @Size(max = 255, message = "Airplane ID must not exceed 255 characters")
    private String airplaneId;

    @NotBlank(message = "Origin airport code is required")
    @Size(max = 10, message = "Origin airport code must not exceed 10 characters")
    private String originAirportCode;

    @NotBlank(message = "Destination airport code is required")
    @Size(max = 10, message = "Destination airport code must not exceed 10 characters")
    private String destinationAirportCode;

    @NotNull(message = "Departure time is required")
    @Future(message = "Departure time must be in the future")
    private LocalDateTime departureTime;

    @NotNull(message = "Arrival time is required")
    private LocalDateTime arrivalTime;

    @Size(max = 50, message = "Terminal must not exceed 50 characters")
    private String terminal;

    @Size(max = 50, message = "Gate must not exceed 50 characters")
    private String gate;

    @Min(value = 0, message = "Baggage allowance must be at least 0")
    private Integer baggageAllowance;

    private String facilities;

    @Min(value = 1, message = "Status must be between 1 and 5")
    private Integer status; // 1=Scheduled, 2=In Flight, 3=Finished, 4=Delayed, 5=Cancelled
}
