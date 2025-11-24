package io.harman.flight_be.dto.coupon;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CouponResponseDTO {
    private UUID id;
    private String name;
    private String description;
    private Integer points;
    private Integer percentOff;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
}
