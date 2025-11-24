package io.harman.flight_be.repository.flight;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import io.harman.flight_be.model.flight.Airplane;

public interface AirplaneRepository extends JpaRepository<Airplane, String> {
    
    // Find airplanes by airline ID
    List<Airplane> findByAirlineId(String airlineId);
    
    // Find airplanes by airline ID (not deleted)
    List<Airplane> findByAirlineIdAndIsDeletedFalse(String airlineId);
    
    // Find airplanes by model (case insensitive, partial match)
    List<Airplane> findByModelContainingIgnoreCase(String model);
    
    // Find airplanes by model (not deleted)
    List<Airplane> findByModelContainingIgnoreCaseAndIsDeletedFalse(String model);
    
    // Find airplanes by manufacture year
    List<Airplane> findByManufactureYear(Integer year);
    
    // Find airplanes by manufacture year range
    List<Airplane> findByManufactureYearBetween(Integer startYear, Integer endYear);
    
    // Find airplanes by seat capacity range
    List<Airplane> findBySeatCapacityBetween(Integer minCapacity, Integer maxCapacity);
    
    // Find airplanes by minimum seat capacity
    List<Airplane> findBySeatCapacityGreaterThanEqual(Integer minCapacity);
    
    // Find active (not deleted) airplanes
    List<Airplane> findByIsDeletedFalse();
    
    // Find deleted airplanes
    List<Airplane> findByIsDeletedTrue();
    
    // Find airplane by ID (not deleted)
    Optional<Airplane> findByIdAndIsDeletedFalse(String id);
    
    // Count airplanes by airline
    long countByAirlineId(String airlineId);
    
    // Count active airplanes by airline
    long countByAirlineIdAndIsDeletedFalse(String airlineId);
    
    // Get all airplanes ordered by manufacture year
    List<Airplane> findAllByOrderByManufactureYearDesc();
    
    // Get airplanes by airline ordered by manufacture year
    List<Airplane> findByAirlineIdOrderByManufactureYearDesc(String airlineId);
    
    // Check if airplane exists by ID (not deleted)
    boolean existsByIdAndIsDeletedFalse(String id);
    
    // Get average seat capacity by airline
    @Query("SELECT AVG(a.seatCapacity) FROM Airplane a WHERE a.airlineId = :airlineId AND a.isDeleted = false")
    Double getAverageSeatCapacityByAirline(@Param("airlineId") String airlineId);
    
    // Find newest airplanes (by manufacture year)
    @Query("SELECT a FROM Airplane a WHERE a.isDeleted = false ORDER BY a.manufactureYear DESC")
    List<Airplane> findNewestAirplanes();
}
