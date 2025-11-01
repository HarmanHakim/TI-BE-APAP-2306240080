package io.harman.flight_be.service;

import io.harman.flight_be.dto.flight.CreateFlightDto;
import io.harman.flight_be.dto.flight.ReadFlightDto;
import io.harman.flight_be.dto.flight.UpdateFlightDto;

import java.time.LocalDateTime;
import java.util.List;

public interface FlightService {
    
    // Basic CRUD operations
    List<ReadFlightDto> getAllFlights();
    
    List<ReadFlightDto> getAllActiveFlights();
    
    ReadFlightDto getFlightById(String id);
    
    ReadFlightDto createFlight(CreateFlightDto createFlightDto);
    
    ReadFlightDto updateFlight(UpdateFlightDto updateFlightDto);
    
    void deleteFlight(String id); // Soft delete & cancel
    
    // Advanced search and filter
    List<ReadFlightDto> searchFlights(String origin, String destination, LocalDateTime departureDate, String airlineId, Integer status);
    
    List<ReadFlightDto> getFlightsByRoute(String origin, String destination);
    
    List<ReadFlightDto> getFlightsByStatus(Integer status);
    
    List<ReadFlightDto> getUpcomingFlights();
    
    List<ReadFlightDto> getFlightsDepartingToday();
    
    List<ReadFlightDto> getFlightsByAirline(String airlineId);
    
    List<ReadFlightDto> getFlightsWithAvailableSeats();
    
    // Utility methods
    String generateFlightId(String airplaneId);
    
    boolean canDeleteFlight(String flightId);
    
    boolean canUpdateFlight(String flightId);
    
    boolean isAirplaneAvailable(String airplaneId, LocalDateTime departureTime, LocalDateTime arrivalTime, String excludeFlightId);
}
