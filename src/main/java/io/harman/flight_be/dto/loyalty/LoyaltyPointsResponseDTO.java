package io.harman.flight_be.dto.loyalty;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoyaltyPointsResponseDTO {
    private UUID customerId;
    private Integer points;
}
