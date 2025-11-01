package io.harman.flight_be.dto.passenger;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdatePassengerDto {
    @NotNull(message = "Passenger ID is required")
    private UUID id;

    @NotBlank(message = "Full name is required")
    @Size(max = 255, message = "Full name must not exceed 255 characters")
    private String fullName;

    @NotNull(message = "Birth date is required")
    @Past(message = "Birth date must be in the past")
    private LocalDate birthDate;

    @NotNull(message = "Gender is required")
    @Min(value = 1, message = "Gender must be 1, 2, or 3")
    @Max(value = 3, message = "Gender must be 1, 2, or 3")
    private Integer gender;

    @NotBlank(message = "ID/Passport is required")
    @Size(max = 100, message = "ID/Passport must not exceed 100 characters")
    private String idPassport;
}
