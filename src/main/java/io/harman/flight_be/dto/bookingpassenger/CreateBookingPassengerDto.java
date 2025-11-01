package io.harman.flight_be.dto.bookingpassenger;

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
public class CreateBookingPassengerDto {
    @NotBlank(message = "Booking ID is required")
    @Size(max = 255, message = "Booking ID must not exceed 255 characters")
    private String bookingId;

    @NotNull(message = "Passenger ID is required")
    private UUID passengerId;
}
