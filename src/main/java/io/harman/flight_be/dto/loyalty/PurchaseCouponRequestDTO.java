package io.harman.flight_be.dto.loyalty;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseCouponRequestDTO {

    @NotNull(message = "Coupon ID is required")
    private UUID couponId;

    @NotNull(message = "Customer ID is required")
    private UUID customerId;
}
