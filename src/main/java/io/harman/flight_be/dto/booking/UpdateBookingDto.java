package io.harman.flight_be.dto.booking;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateBookingDto {
    @NotBlank(message = "Booking ID is required")
    @Size(max = 255, message = "Booking ID must not exceed 255 characters")
    private String id;

    @NotBlank(message = "Flight ID is required")
    @Size(max = 255, message = "Flight ID must not exceed 255 characters")
    private String flightId;

    @NotNull(message = "Class Flight ID is required")
    private Integer classFlightId;

    @NotBlank(message = "Contact email is required")
    @Email(message = "Contact email must be valid")
    @Size(max = 255, message = "Contact email must not exceed 255 characters")
    private String contactEmail;

    @NotBlank(message = "Contact phone is required")
    @Size(max = 20, message = "Contact phone must not exceed 20 characters")
    private String contactPhone;

    @NotNull(message = "Passenger count is required")
    @Min(value = 1, message = "Passenger count must be at least 1")
    private Integer passengerCount;

    @NotNull(message = "Status is required")
    @Min(value = 1, message = "Status must be between 1 and 4")
    @Max(value = 4, message = "Status must be between 1 and 4")
    private Integer status;

    @NotNull(message = "Total price is required")
    @DecimalMin(value = "0.00", message = "Total price must be at least 0.00")
    private BigDecimal totalPrice;

    private List<UUID> passengerIds;
}
