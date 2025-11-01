package io.harman.flight_be.dto.flight;

import io.harman.flight_be.dto.rest.BaseResponseDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class FlightResponseDto extends BaseResponseDTO<ReadFlightDto> {
    
    public FlightResponseDto(int status, String message, ReadFlightDto data) {
        super(status, message, new Date(), data);
    }
    
    public static FlightResponseDto success(ReadFlightDto data) {
        return new FlightResponseDto(200, "Success", data);
    }
    
    public static FlightResponseDto created(ReadFlightDto data) {
        return new FlightResponseDto(201, "Flight created successfully", data);
    }
    
    public static FlightResponseDto updated(ReadFlightDto data) {
        return new FlightResponseDto(200, "Flight updated successfully", data);
    }
    
    public static FlightResponseDto cancelled() {
        FlightResponseDto response = new FlightResponseDto();
        response.setStatus(200);
        response.setMessage("Flight cancelled successfully");
        response.setTimestamp(new Date());
        return response;
    }
    
    public static FlightResponseDto deleted() {
        FlightResponseDto response = new FlightResponseDto();
        response.setStatus(200);
        response.setMessage("Flight deleted successfully");
        response.setTimestamp(new Date());
        return response;
    }
    
    public static FlightResponseDto error(int status, String message) {
        FlightResponseDto response = new FlightResponseDto();
        response.setStatus(status);
        response.setMessage(message);
        response.setTimestamp(new Date());
        return response;
    }
}
