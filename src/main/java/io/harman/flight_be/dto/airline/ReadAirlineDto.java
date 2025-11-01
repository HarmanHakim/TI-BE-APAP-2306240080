package io.harman.flight_be.dto.airline;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReadAirlineDto {
    private String id;
    private String name;
    private String country;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
