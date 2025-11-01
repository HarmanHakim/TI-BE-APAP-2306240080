package io.harman.flight_be.dto.booking;

import io.harman.flight_be.dto.rest.BaseResponseDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class BookingListResponseDto extends BaseResponseDTO<List<ReadBookingDto>> {
    
    public BookingListResponseDto(int status, String message, List<ReadBookingDto> data) {
        super(status, message, new Date(), data);
    }
    
    public static BookingListResponseDto success(List<ReadBookingDto> data) {
        return new BookingListResponseDto(200, "Success", data);
    }
    
    public static BookingListResponseDto error(int status, String message) {
        BookingListResponseDto response = new BookingListResponseDto();
        response.setStatus(status);
        response.setMessage(message);
        response.setTimestamp(new Date());
        return response;
    }
}
