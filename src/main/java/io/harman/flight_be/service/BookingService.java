package io.harman.flight_be.service;

import io.harman.flight_be.dto.booking.CreateBookingDto;
import io.harman.flight_be.dto.booking.ReadBookingDto;
import io.harman.flight_be.dto.booking.UpdateBookingDto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface BookingService {
    
    // Basic CRUD operations
    List<ReadBookingDto> getAllBookings();
    
    List<ReadBookingDto> getAllActiveBookings();
    
    ReadBookingDto getBookingById(String id);
    
    ReadBookingDto createBooking(CreateBookingDto createBookingDto);
    
    ReadBookingDto updateBooking(UpdateBookingDto updateBookingDto);
    
    void deleteBooking(String id); // Soft delete & cancel
    
    // Advanced search and filter
    List<ReadBookingDto> getBookingsByStatus(Integer status);
    
    List<ReadBookingDto> getBookingsByEmail(String email);
    
    List<ReadBookingDto> getBookingsByFlightId(String flightId);
    
    List<ReadBookingDto> getBookingsByDateRange(LocalDateTime start, LocalDateTime end);
    
    // Statistics
    Map<String, Object> getBookingStatistics(LocalDateTime start, LocalDateTime end);
    
    BigDecimal getTotalRevenue(LocalDateTime start, LocalDateTime end);
    
    BigDecimal getTotalRevenueByFlight(String flightId);
    
    // Utility methods
    String generateBookingId(String flightId, String originAirportCode, String destinationAirportCode);
    
    boolean canUpdateBooking(String bookingId);
    
    boolean canDeleteBooking(String bookingId);
}
