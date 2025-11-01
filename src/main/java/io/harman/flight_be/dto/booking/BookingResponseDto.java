package io.harman.flight_be.dto.booking;

import io.harman.flight_be.dto.rest.BaseResponseDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class BookingResponseDto extends BaseResponseDTO<ReadBookingDto> {
    
    public BookingResponseDto(int status, String message, ReadBookingDto data) {
        super(status, message, new Date(), data);
    }
    
    public static BookingResponseDto success(ReadBookingDto data) {
        return new BookingResponseDto(200, "Success", data);
    }
    
    public static BookingResponseDto created(ReadBookingDto data) {
        return new BookingResponseDto(201, "Booking created successfully", data);
    }
    
    public static BookingResponseDto updated(ReadBookingDto data) {
        return new BookingResponseDto(200, "Booking updated successfully", data);
    }
    
    public static BookingResponseDto cancelled() {
        BookingResponseDto response = new BookingResponseDto();
        response.setStatus(200);
        response.setMessage("Booking cancelled successfully");
        response.setTimestamp(new Date());
        return response;
    }
    
    public static BookingResponseDto deleted() {
        BookingResponseDto response = new BookingResponseDto();
        response.setStatus(200);
        response.setMessage("Booking deleted successfully");
        response.setTimestamp(new Date());
        return response;
    }
    
    public static BookingResponseDto error(int status, String message) {
        BookingResponseDto response = new BookingResponseDto();
        response.setStatus(status);
        response.setMessage(message);
        response.setTimestamp(new Date());
        return response;
    }
}
