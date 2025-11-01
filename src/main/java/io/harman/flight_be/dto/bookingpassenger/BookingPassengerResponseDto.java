package io.harman.flight_be.dto.bookingpassenger;

import io.harman.flight_be.dto.rest.BaseResponseDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class BookingPassengerResponseDto extends BaseResponseDTO<ReadBookingPassengerDto> {
    
    public BookingPassengerResponseDto(int status, String message, ReadBookingPassengerDto data) {
        super(status, message, new Date(), data);
    }
    
    public static BookingPassengerResponseDto success(ReadBookingPassengerDto data) {
        return new BookingPassengerResponseDto(200, "Success", data);
    }
    
    public static BookingPassengerResponseDto created(ReadBookingPassengerDto data) {
        return new BookingPassengerResponseDto(201, "Passenger added to booking successfully", data);
    }
    
    public static BookingPassengerResponseDto deleted() {
        BookingPassengerResponseDto response = new BookingPassengerResponseDto();
        response.setStatus(200);
        response.setMessage("Passenger removed from booking successfully");
        response.setTimestamp(new Date());
        return response;
    }
    
    public static BookingPassengerResponseDto error(int status, String message) {
        BookingPassengerResponseDto response = new BookingPassengerResponseDto();
        response.setStatus(status);
        response.setMessage(message);
        response.setTimestamp(new Date());
        return response;
    }
}
