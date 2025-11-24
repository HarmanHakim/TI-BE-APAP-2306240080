package io.harman.flight_be.dto.coupon;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateCouponRequestDTO {

    @NotBlank(message = "Coupon name is required")
    private String name;

    private String description;

    @NotNull(message = "Points is required")
    @Min(value = 1, message = "Points must be at least 1")
    private Integer points;

    @NotNull(message = "Percent off is required")
    @Min(value = 1, message = "Percent off must be greater than 0")
    @Max(value = 100, message = "Percent off cannot be more than 100")
    private Integer percentOff;
}
