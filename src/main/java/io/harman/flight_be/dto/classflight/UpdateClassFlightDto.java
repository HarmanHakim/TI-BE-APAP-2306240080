package io.harman.flight_be.dto.classflight;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateClassFlightDto {
    @NotNull(message = "Class Flight ID is required")
    private Integer id;

    @NotBlank(message = "Flight ID is required")
    @Size(max = 255, message = "Flight ID must not exceed 255 characters")
    private String flightId;

    @NotBlank(message = "Class type is required")
    @Size(max = 50, message = "Class type must not exceed 50 characters")
    private String classType;

    @NotNull(message = "Seat capacity is required")
    @Min(value = 1, message = "Seat capacity must be at least 1")
    private Integer seatCapacity;

    @NotNull(message = "Available seats is required")
    @Min(value = 0, message = "Available seats must be at least 0")
    private Integer availableSeats;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.00", message = "Price must be at least 0.00")
    private BigDecimal price;
}
