package io.harman.flight_be.dto.classflight;

import io.harman.flight_be.dto.rest.BaseResponseDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class ClassFlightResponseDto extends BaseResponseDTO<ReadClassFlightDto> {
    
    public ClassFlightResponseDto(int status, String message, ReadClassFlightDto data) {
        super(status, message, new Date(), data);
    }
    
    public static ClassFlightResponseDto success(ReadClassFlightDto data) {
        return new ClassFlightResponseDto(200, "Success", data);
    }
    
    public static ClassFlightResponseDto created(ReadClassFlightDto data) {
        return new ClassFlightResponseDto(201, "Flight class created successfully", data);
    }
    
    public static ClassFlightResponseDto updated(ReadClassFlightDto data) {
        return new ClassFlightResponseDto(200, "Flight class updated successfully", data);
    }
    
    public static ClassFlightResponseDto deleted() {
        ClassFlightResponseDto response = new ClassFlightResponseDto();
        response.setStatus(200);
        response.setMessage("Flight class deleted successfully");
        response.setTimestamp(new Date());
        return response;
    }
    
    public static ClassFlightResponseDto error(int status, String message) {
        ClassFlightResponseDto response = new ClassFlightResponseDto();
        response.setStatus(status);
        response.setMessage(message);
        response.setTimestamp(new Date());
        return response;
    }
}
