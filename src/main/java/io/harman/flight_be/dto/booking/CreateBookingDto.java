package io.harman.flight_be.dto.booking;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
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
public class CreateBookingDto {
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

    @NotNull(message = "Total price is required")
    @DecimalMin(value = "0.00", message = "Total price must be at least 0.00")
    private BigDecimal totalPrice;

    @Min(value = 1, message = "Status must be between 1 and 4")
    @Max(value = 4, message = "Status must be between 1 and 4")
    private Integer status; // 1=Unpaid, 2=Paid, 3=Cancelled, 4=Rescheduled

    @NotNull(message = "Passenger IDs are required")
    @Size(min = 1, message = "At least one passenger is required")
    private List<UUID> passengerIds;
}
