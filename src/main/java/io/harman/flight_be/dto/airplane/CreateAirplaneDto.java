package io.harman.flight_be.dto.airplane;

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
public class CreateAirplaneDto {
    @NotBlank(message = "Airline ID is required")
    @Size(max = 3, min = 3, message = "Airline ID must be exactly 3 characters")
    private String airlineId;

    @NotBlank(message = "Model is required")
    @Size(max = 255, message = "Model must not exceed 255 characters")
    private String model;

    @NotNull(message = "Seat capacity is required")
    @Min(value = 1, message = "Seat capacity must be at least 1")
    private Integer seatCapacity;

    @NotNull(message = "Manufacture year is required")
    @Min(value = 1900, message = "Manufacture year must be at least 1900")
    private Integer manufactureYear;
}
