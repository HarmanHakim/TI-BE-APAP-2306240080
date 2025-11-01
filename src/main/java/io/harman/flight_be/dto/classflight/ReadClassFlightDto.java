package io.harman.flight_be.dto.classflight;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReadClassFlightDto {
    private Integer id;
    private String flightId;
    private String flightNumber;
    private String classType;
    private Integer seatCapacity;
    private Integer availableSeats;
    private BigDecimal price;
}
