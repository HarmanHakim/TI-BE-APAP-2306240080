package io.harman.flight_be.dto.airline;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateAirlineDto {
    @NotBlank(message = "Airline ID is required")
    @Size(max = 3, min = 3, message = "Airline ID must be exactly 3 characters")
    private String id;

    @NotBlank(message = "Airline name is required")
    @Size(max = 255, message = "Airline name must not exceed 255 characters")
    private String name;

    @NotBlank(message = "Country is required")
    @Size(max = 100, message = "Country must not exceed 100 characters")
    private String country;
}
