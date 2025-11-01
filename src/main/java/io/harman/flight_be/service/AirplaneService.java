package io.harman.flight_be.service;

import io.harman.flight_be.dto.airplane.CreateAirplaneDto;
import io.harman.flight_be.dto.airplane.ReadAirplaneDto;
import io.harman.flight_be.dto.airplane.UpdateAirplaneDto;

import java.util.List;

public interface AirplaneService {
    
    // Basic CRUD operations
    List<ReadAirplaneDto> getAllAirplanes();
    
    List<ReadAirplaneDto> getAllActiveAirplanes();
    
    ReadAirplaneDto getAirplaneById(String id);
    
    ReadAirplaneDto createAirplane(CreateAirplaneDto createAirplaneDto);
    
    ReadAirplaneDto updateAirplane(UpdateAirplaneDto updateAirplaneDto);
    
    void deleteAirplane(String id); // Soft delete
    
    void activateAirplane(String id); // Restore soft deleted
    
    // Advanced search and filter
    List<ReadAirplaneDto> getAirplanesByAirlineId(String airlineId);
    
    List<ReadAirplaneDto> searchAirplanes(String airlineId, String model, Integer manufactureYear, Boolean isDeleted);
    
    List<ReadAirplaneDto> getAirplanesByManufactureYearRange(Integer startYear, Integer endYear);
    
    // Utility methods
    String generateAirplaneId(String airlineId);
    
    boolean canDeleteAirplane(String airplaneId);
}
