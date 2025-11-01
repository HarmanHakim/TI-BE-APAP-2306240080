package io.harman.flight_be.dto.airplane;

import io.harman.flight_be.dto.rest.BaseResponseDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class AirplaneResponseDto extends BaseResponseDTO<ReadAirplaneDto> {
    
    public AirplaneResponseDto(int status, String message, ReadAirplaneDto data) {
        super(status, message, new Date(), data);
    }
    
    public static AirplaneResponseDto success(ReadAirplaneDto data) {
        return new AirplaneResponseDto(200, "Success", data);
    }
    
    public static AirplaneResponseDto created(ReadAirplaneDto data) {
        return new AirplaneResponseDto(201, "Airplane created successfully", data);
    }
    
    public static AirplaneResponseDto updated(ReadAirplaneDto data) {
        return new AirplaneResponseDto(200, "Airplane updated successfully", data);
    }
    
    public static AirplaneResponseDto deleted() {
        AirplaneResponseDto response = new AirplaneResponseDto();
        response.setStatus(200);
        response.setMessage("Airplane deleted successfully");
        response.setTimestamp(new Date());
        return response;
    }
    
    public static AirplaneResponseDto error(int status, String message) {
        AirplaneResponseDto response = new AirplaneResponseDto();
        response.setStatus(status);
        response.setMessage(message);
        response.setTimestamp(new Date());
        return response;
    }
}
