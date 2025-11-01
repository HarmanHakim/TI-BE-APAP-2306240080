package io.harman.flight_be.dto.airplane;

import io.harman.flight_be.dto.rest.BaseResponseDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class AirplaneListResponseDto extends BaseResponseDTO<List<ReadAirplaneDto>> {
    
    public AirplaneListResponseDto(int status, String message, List<ReadAirplaneDto> data) {
        super(status, message, new Date(), data);
    }
    
    public static AirplaneListResponseDto success(List<ReadAirplaneDto> data) {
        return new AirplaneListResponseDto(200, "Success", data);
    }
    
    public static AirplaneListResponseDto error(int status, String message) {
        AirplaneListResponseDto response = new AirplaneListResponseDto();
        response.setStatus(status);
        response.setMessage(message);
        response.setTimestamp(new Date());
        return response;
    }
}
