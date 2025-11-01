package io.harman.flight_be.dto.bookingpassenger;

import io.harman.flight_be.dto.rest.BaseResponseDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class BookingPassengerListResponseDto extends BaseResponseDTO<List<ReadBookingPassengerDto>> {
    
    public BookingPassengerListResponseDto(int status, String message, List<ReadBookingPassengerDto> data) {
        super(status, message, new Date(), data);
    }
    
    public static BookingPassengerListResponseDto success(List<ReadBookingPassengerDto> data) {
        return new BookingPassengerListResponseDto(200, "Success", data);
    }
    
    public static BookingPassengerListResponseDto error(int status, String message) {
        BookingPassengerListResponseDto response = new BookingPassengerListResponseDto();
        response.setStatus(status);
        response.setMessage(message);
        response.setTimestamp(new Date());
        return response;
    }
}
