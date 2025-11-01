package io.harman.flight_be.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import io.harman.flight_be.model.Airline;

public interface AirlineRepository extends JpaRepository<Airline, String> {
    
    // Find airlines by name (case insensitive, partial match)
    List<Airline> findByNameContainingIgnoreCase(String name);
    
    // Find airlines by country
    List<Airline> findByCountry(String country);
    
    // Find airlines by country (case insensitive)
    List<Airline> findByCountryIgnoreCase(String country);
    
    // Check if airline exists by name
    boolean existsByName(String name);
    
    // Check if airline exists by name (case insensitive)
    @Query("SELECT CASE WHEN COUNT(a) > 0 THEN true ELSE false END FROM Airline a WHERE LOWER(a.name) = LOWER(:name)")
    boolean existsByNameIgnoreCase(@Param("name") String name);
    
    // Find airline by name (exact match)
    Optional<Airline> findByName(String name);
    
    // Get all airlines ordered by name
    List<Airline> findAllByOrderByNameAsc();
    
    // Get all airlines from specific country ordered by name
    List<Airline> findByCountryOrderByNameAsc(String country);
    
    // Count airlines by country
    @Query("SELECT COUNT(a) FROM Airline a WHERE a.country = :country")
    long countByCountry(@Param("country") String country);
    
    // Get distinct countries
    @Query("SELECT DISTINCT a.country FROM Airline a ORDER BY a.country")
    List<String> findDistinctCountries();
}
