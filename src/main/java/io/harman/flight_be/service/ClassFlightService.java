package io.harman.flight_be.service;

import io.harman.flight_be.dto.classflight.CreateClassFlightDto;
import io.harman.flight_be.dto.classflight.ReadClassFlightDto;
import io.harman.flight_be.dto.classflight.UpdateClassFlightDto;

import java.util.List;

public interface ClassFlightService {
    
    // Basic CRUD operations
    List<ReadClassFlightDto> getAllClassFlights();
    
    ReadClassFlightDto getClassFlightById(Integer id);
    
    ReadClassFlightDto createClassFlight(CreateClassFlightDto createClassFlightDto);
    
    ReadClassFlightDto updateClassFlight(UpdateClassFlightDto updateClassFlightDto);
    
    void deleteClassFlight(Integer id);
    
    // Search and filter operations
    List<ReadClassFlightDto> getClassFlightsByFlightId(String flightId);
    
    List<ReadClassFlightDto> getClassFlightsWithAvailableSeats();
    
    ReadClassFlightDto getClassFlightByFlightIdAndType(String flightId, String classType);
    
    // Seat management
    void decreaseAvailableSeats(Integer classFlightId, Integer count);
    
    void increaseAvailableSeats(Integer classFlightId, Integer count);
    
    Integer getTotalAvailableSeatsByFlight(String flightId);
}
