package io.harman.flight_be.dto.airline;

import io.harman.flight_be.dto.rest.BaseResponseDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class AirlineListResponseDto extends BaseResponseDTO<List<ReadAirlineDto>> {
    
    public AirlineListResponseDto(int status, String message, List<ReadAirlineDto> data) {
        super(status, message, new Date(), data);
    }
    
    public static AirlineListResponseDto success(List<ReadAirlineDto> data) {
        return new AirlineListResponseDto(200, "Success", data);
    }
    
    public static AirlineListResponseDto error(int status, String message) {
        AirlineListResponseDto response = new AirlineListResponseDto();
        response.setStatus(status);
        response.setMessage(message);
        response.setTimestamp(new Date());
        return response;
    }
}
