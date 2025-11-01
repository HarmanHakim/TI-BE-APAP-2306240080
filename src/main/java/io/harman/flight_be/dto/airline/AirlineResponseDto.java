package io.harman.flight_be.dto.airline;

import io.harman.flight_be.dto.rest.BaseResponseDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class AirlineResponseDto extends BaseResponseDTO<ReadAirlineDto> {
    
    public AirlineResponseDto(int status, String message, ReadAirlineDto data) {
        super(status, message, new Date(), data);
    }
    
    public static AirlineResponseDto success(ReadAirlineDto data) {
        return new AirlineResponseDto(200, "Success", data);
    }
    
    public static AirlineResponseDto created(ReadAirlineDto data) {
        return new AirlineResponseDto(201, "Airline created successfully", data);
    }
    
    public static AirlineResponseDto updated(ReadAirlineDto data) {
        return new AirlineResponseDto(200, "Airline updated successfully", data);
    }
    
    public static AirlineResponseDto deleted() {
        AirlineResponseDto response = new AirlineResponseDto();
        response.setStatus(200);
        response.setMessage("Airline deleted successfully");
        response.setTimestamp(new Date());
        return response;
    }
    
    public static AirlineResponseDto error(int status, String message) {
        AirlineResponseDto response = new AirlineResponseDto();
        response.setStatus(status);
        response.setMessage(message);
        response.setTimestamp(new Date());
        return response;
    }
}
