package io.harman.flight_be.dto.airplane;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReadAirplaneDto {
    private String id;
    private String airlineId;
    private String airlineName;
    private String model;
    private Integer seatCapacity;
    private Integer manufactureYear;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Boolean isDeleted;
}
