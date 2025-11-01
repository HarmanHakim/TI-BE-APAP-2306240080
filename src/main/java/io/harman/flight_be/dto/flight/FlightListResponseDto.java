package io.harman.flight_be.dto.flight;

import io.harman.flight_be.dto.rest.BaseResponseDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class FlightListResponseDto extends BaseResponseDTO<List<ReadFlightDto>> {
    
    public FlightListResponseDto(int status, String message, List<ReadFlightDto> data) {
        super(status, message, new Date(), data);
    }
    
    public static FlightListResponseDto success(List<ReadFlightDto> data) {
        return new FlightListResponseDto(200, "Success", data);
    }
    
    public static FlightListResponseDto error(int status, String message) {
        FlightListResponseDto response = new FlightListResponseDto();
        response.setStatus(status);
        response.setMessage(message);
        response.setTimestamp(new Date());
        return response;
    }
}
