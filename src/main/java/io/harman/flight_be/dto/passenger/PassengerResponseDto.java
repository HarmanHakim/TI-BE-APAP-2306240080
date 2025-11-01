package io.harman.flight_be.dto.passenger;

import io.harman.flight_be.dto.rest.BaseResponseDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class PassengerResponseDto extends BaseResponseDTO<ReadPassengerDto> {
    
    public PassengerResponseDto(int status, String message, ReadPassengerDto data) {
        super(status, message, new Date(), data);
    }
    
    public static PassengerResponseDto success(ReadPassengerDto data) {
        return new PassengerResponseDto(200, "Success", data);
    }
    
    public static PassengerResponseDto created(ReadPassengerDto data) {
        return new PassengerResponseDto(201, "Passenger created successfully", data);
    }
    
    public static PassengerResponseDto updated(ReadPassengerDto data) {
        return new PassengerResponseDto(200, "Passenger updated successfully", data);
    }
    
    public static PassengerResponseDto deleted() {
        PassengerResponseDto response = new PassengerResponseDto();
        response.setStatus(200);
        response.setMessage("Passenger deleted successfully");
        response.setTimestamp(new Date());
        return response;
    }
    
    public static PassengerResponseDto error(int status, String message) {
        PassengerResponseDto response = new PassengerResponseDto();
        response.setStatus(status);
        response.setMessage(message);
        response.setTimestamp(new Date());
        return response;
    }
}
