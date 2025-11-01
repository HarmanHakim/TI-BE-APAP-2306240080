package io.harman.flight_be.service;

import io.harman.flight_be.dto.passenger.CreatePassengerDto;
import io.harman.flight_be.dto.passenger.ReadPassengerDto;
import io.harman.flight_be.dto.passenger.UpdatePassengerDto;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface PassengerService {
    
    // Basic CRUD operations
    List<ReadPassengerDto> getAllPassengers();
    
    ReadPassengerDto getPassengerById(UUID id);
    
    ReadPassengerDto createPassenger(CreatePassengerDto createPassengerDto);
    
    ReadPassengerDto updatePassenger(UpdatePassengerDto updatePassengerDto);
    
    void deletePassenger(UUID id);
    
    // Search operations
    List<ReadPassengerDto> searchPassengers(String fullName, String idPassport, Integer gender);
    
    ReadPassengerDto getPassengerByIdPassport(String idPassport);
    
    List<ReadPassengerDto> getPassengersByGender(Integer gender);
    
    List<ReadPassengerDto> getAdultPassengers();
    
    List<ReadPassengerDto> getChildPassengers();
    
    // Utility methods
    int calculateAge(LocalDate birthDate);
    
    String getGenderLabel(Integer gender);
}
