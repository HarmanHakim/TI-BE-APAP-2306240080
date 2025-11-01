package io.harman.flight_be.service;

import io.harman.flight_be.dto.seat.CreateSeatDto;
import io.harman.flight_be.dto.seat.ReadSeatDto;
import io.harman.flight_be.dto.seat.UpdateSeatDto;

import java.util.List;
import java.util.UUID;

public interface SeatService {
    
    // Basic CRUD operations
    List<ReadSeatDto> getAllSeats();
    
    ReadSeatDto getSeatById(Long id);
    
    ReadSeatDto createSeat(CreateSeatDto createSeatDto);
    
    ReadSeatDto updateSeat(UpdateSeatDto updateSeatDto);
    
    void deleteSeat(Long id);
    
    // Search and filter operations
    List<ReadSeatDto> getSeatsByClassFlightId(Integer classFlightId);
    
    List<ReadSeatDto> getAvailableSeatsByClassFlightId(Integer classFlightId);
    
    List<ReadSeatDto> getSeatsByFlightId(String flightId);
    
    List<ReadSeatDto> getAvailableSeatsByFlightId(String flightId);
    
    // Seat assignment operations
    ReadSeatDto assignSeatToPassenger(Long seatId, UUID passengerId);
    
    ReadSeatDto releaseSeat(Long seatId);
    
    void releaseSeatsByPassenger(UUID passengerId);
    
    // Utility methods
    boolean isSeatAvailable(Long seatId);
    
    String generateSeatCode(String flightId, String classType, Long seatId);
}
