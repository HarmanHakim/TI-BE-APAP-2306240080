package io.harman.flight_be.dto.seat;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateSeatDto {
    @NotNull(message = "Seat ID is required")
    private Long id;

    @NotNull(message = "Class Flight ID is required")
    private Integer classFlightId;

    @NotBlank(message = "Seat number is required")
    @Size(max = 10, message = "Seat number must not exceed 10 characters")
    private String seatNumber;

    @NotNull(message = "Availability status is required")
    private Boolean isAvailable;

    private UUID passengerId;
}
