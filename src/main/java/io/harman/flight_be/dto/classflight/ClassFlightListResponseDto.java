package io.harman.flight_be.dto.classflight;

import io.harman.flight_be.dto.rest.BaseResponseDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class ClassFlightListResponseDto extends BaseResponseDTO<List<ReadClassFlightDto>> {
    
    public ClassFlightListResponseDto(int status, String message, List<ReadClassFlightDto> data) {
        super(status, message, new Date(), data);
    }
    
    public static ClassFlightListResponseDto success(List<ReadClassFlightDto> data) {
        return new ClassFlightListResponseDto(200, "Success", data);
    }
    
    public static ClassFlightListResponseDto error(int status, String message) {
        ClassFlightListResponseDto response = new ClassFlightListResponseDto();
        response.setStatus(status);
        response.setMessage(message);
        response.setTimestamp(new Date());
        return response;
    }
}
