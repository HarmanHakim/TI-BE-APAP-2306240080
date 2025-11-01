package io.harman.flight_be.dto.seat;

import io.harman.flight_be.dto.rest.BaseResponseDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class SeatResponseDto extends BaseResponseDTO<ReadSeatDto> {
    
    public SeatResponseDto(int status, String message, ReadSeatDto data) {
        super(status, message, new Date(), data);
    }
    
    public static SeatResponseDto success(ReadSeatDto data) {
        return new SeatResponseDto(200, "Success", data);
    }
    
    public static SeatResponseDto created(ReadSeatDto data) {
        return new SeatResponseDto(201, "Seat created successfully", data);
    }
    
    public static SeatResponseDto updated(ReadSeatDto data) {
        return new SeatResponseDto(200, "Seat updated successfully", data);
    }
    
    public static SeatResponseDto assigned(ReadSeatDto data) {
        return new SeatResponseDto(200, "Seat assigned successfully", data);
    }
    
    public static SeatResponseDto released(ReadSeatDto data) {
        return new SeatResponseDto(200, "Seat released successfully", data);
    }
    
    public static SeatResponseDto deleted() {
        SeatResponseDto response = new SeatResponseDto();
        response.setStatus(200);
        response.setMessage("Seat deleted successfully");
        response.setTimestamp(new Date());
        return response;
    }
    
    public static SeatResponseDto error(int status, String message) {
        SeatResponseDto response = new SeatResponseDto();
        response.setStatus(status);
        response.setMessage(message);
        response.setTimestamp(new Date());
        return response;
    }
}
