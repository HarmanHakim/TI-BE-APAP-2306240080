package io.harman.flight_be.dto.seat;

import io.harman.flight_be.dto.rest.BaseResponseDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class SeatListResponseDto extends BaseResponseDTO<List<ReadSeatDto>> {
    
    public SeatListResponseDto(int status, String message, List<ReadSeatDto> data) {
        super(status, message, new Date(), data);
    }
    
    public static SeatListResponseDto success(List<ReadSeatDto> data) {
        return new SeatListResponseDto(200, "Success", data);
    }
    
    public static SeatListResponseDto error(int status, String message) {
        SeatListResponseDto response = new SeatListResponseDto();
        response.setStatus(status);
        response.setMessage(message);
        response.setTimestamp(new Date());
        return response;
    }
}
