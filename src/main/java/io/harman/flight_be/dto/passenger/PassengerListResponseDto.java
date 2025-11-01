package io.harman.flight_be.dto.passenger;

import io.harman.flight_be.dto.rest.BaseResponseDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class PassengerListResponseDto extends BaseResponseDTO<List<ReadPassengerDto>> {
    
    public PassengerListResponseDto(int status, String message, List<ReadPassengerDto> data) {
        super(status, message, new Date(), data);
    }
    
    public static PassengerListResponseDto success(List<ReadPassengerDto> data) {
        return new PassengerListResponseDto(200, "Success", data);
    }
    
    public static PassengerListResponseDto error(int status, String message) {
        PassengerListResponseDto response = new PassengerListResponseDto();
        response.setStatus(status);
        response.setMessage(message);
        response.setTimestamp(new Date());
        return response;
    }
}
