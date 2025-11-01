package io.harman.flight_be.repository;

import io.harman.flight_be.model.ClassFlight;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface ClassFlightRepository extends JpaRepository<ClassFlight, Integer> {
    
    // Find class flights by flight ID
    List<ClassFlight> findByFlightId(String flightId);
    
    // Find class flight by flight ID and class type
    Optional<ClassFlight> findByFlightIdAndClassType(String flightId, String classType);
    
    // Find class flights by class type
    List<ClassFlight> findByClassType(String classType);
    
    // Find class flights by class type (case insensitive)
    List<ClassFlight> findByClassTypeIgnoreCase(String classType);
    
    // Find class flights with available seats
    @Query("SELECT cf FROM ClassFlight cf WHERE cf.availableSeats > 0")
    List<ClassFlight> findAllWithAvailableSeats();
    
    // Find class flights with available seats by flight ID
    @Query("SELECT cf FROM ClassFlight cf WHERE cf.flightId = :flightId AND cf.availableSeats > 0")
    List<ClassFlight> findByFlightIdWithAvailableSeats(@Param("flightId") String flightId);
    
    // Find class flights with available seats by class type
    @Query("SELECT cf FROM ClassFlight cf WHERE cf.classType = :classType AND cf.availableSeats > 0")
    List<ClassFlight> findByClassTypeWithAvailableSeats(@Param("classType") String classType);
    
    // Find class flights by minimum available seats
    List<ClassFlight> findByAvailableSeatsGreaterThanEqual(Integer minSeats);
    
    // Find class flights by price range
    List<ClassFlight> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice);
    
    // Find class flights by flight ID and price range
    List<ClassFlight> findByFlightIdAndPriceBetween(String flightId, BigDecimal minPrice, BigDecimal maxPrice);
    
    // Count class flights by flight ID
    long countByFlightId(String flightId);
    
    // Check if class flight exists for flight and type
    boolean existsByFlightIdAndClassType(String flightId, String classType);
    
    // Get cheapest class flight by flight ID
    @Query("SELECT cf FROM ClassFlight cf WHERE cf.flightId = :flightId ORDER BY cf.price ASC")
    List<ClassFlight> findByFlightIdOrderByPriceAsc(@Param("flightId") String flightId);
    
    // Get most expensive class flight by flight ID
    @Query("SELECT cf FROM ClassFlight cf WHERE cf.flightId = :flightId ORDER BY cf.price DESC")
    List<ClassFlight> findByFlightIdOrderByPriceDesc(@Param("flightId") String flightId);
    
    // Update available seats
    @Modifying
    @Query("UPDATE ClassFlight cf SET cf.availableSeats = :availableSeats WHERE cf.id = :id")
    int updateAvailableSeats(@Param("id") Integer id, @Param("availableSeats") Integer availableSeats);
    
    // Decrease available seats
    @Modifying
    @Query("UPDATE ClassFlight cf SET cf.availableSeats = cf.availableSeats - :count WHERE cf.id = :id AND cf.availableSeats >= :count")
    int decreaseAvailableSeats(@Param("id") Integer id, @Param("count") Integer count);
    
    // Increase available seats
    @Modifying
    @Query("UPDATE ClassFlight cf SET cf.availableSeats = cf.availableSeats + :count WHERE cf.id = :id")
    int increaseAvailableSeats(@Param("id") Integer id, @Param("count") Integer count);
    
    // Get total capacity by flight
    @Query("SELECT SUM(cf.seatCapacity) FROM ClassFlight cf WHERE cf.flightId = :flightId")
    Integer getTotalCapacityByFlight(@Param("flightId") String flightId);
    
    // Get total available seats by flight
    @Query("SELECT SUM(cf.availableSeats) FROM ClassFlight cf WHERE cf.flightId = :flightId")
    Integer getTotalAvailableSeatsByFlight(@Param("flightId") String flightId);
    
    // Find fully booked classes
    @Query("SELECT cf FROM ClassFlight cf WHERE cf.availableSeats = 0")
    List<ClassFlight> findFullyBookedClasses();
    
    // Find fully booked classes by flight
    @Query("SELECT cf FROM ClassFlight cf WHERE cf.flightId = :flightId AND cf.availableSeats = 0")
    List<ClassFlight> findFullyBookedClassesByFlight(@Param("flightId") String flightId);
}
