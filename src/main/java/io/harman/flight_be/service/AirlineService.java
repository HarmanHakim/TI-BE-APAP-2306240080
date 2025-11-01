package io.harman.flight_be.service;

import io.harman.flight_be.dto.airline.CreateAirlineDto;
import io.harman.flight_be.dto.airline.ReadAirlineDto;
import io.harman.flight_be.dto.airline.UpdateAirlineDto;

import java.util.List;

public interface AirlineService {
    
    // Basic CRUD operations
    List<ReadAirlineDto> getAllAirlines();
    
    ReadAirlineDto getAirlineById(String id);
    
    ReadAirlineDto createAirline(CreateAirlineDto createAirlineDto);
    
    ReadAirlineDto updateAirline(UpdateAirlineDto updateAirlineDto);
    
    void deleteAirline(String id);
    
    // Additional operations
    List<ReadAirlineDto> getAirlinesByCountry(String country);
    
    List<ReadAirlineDto> searchAirlinesByName(String name);
    
    List<String> getDistinctCountries();
    
    long countAirlinesByCountry(String country);
    
    boolean existsById(String id);
}
