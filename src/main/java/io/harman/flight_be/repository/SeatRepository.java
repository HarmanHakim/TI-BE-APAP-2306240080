package io.harman.flight_be.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import io.harman.flight_be.model.Seat;

public interface SeatRepository extends JpaRepository<Seat, Long> {
    
    // Find seats by class flight ID
    List<Seat> findByClassFlightId(Integer classFlightId);
    
    // Find available seats by class flight ID
    List<Seat> findByClassFlightIdAndIsAvailableTrue(Integer classFlightId);
    
    // Find occupied seats by class flight ID
    List<Seat> findByClassFlightIdAndIsAvailableFalse(Integer classFlightId);
    
    // Find seat by class flight ID and seat number
    Optional<Seat> findByClassFlightIdAndSeatNumber(Integer classFlightId, String seatNumber);
    
    // Find seats by passenger ID
    List<Seat> findByPassengerId(UUID passengerId);
    
    // Find seats by class flight ID and passenger ID
    List<Seat> findByClassFlightIdAndPassengerId(Integer classFlightId, UUID passengerId);
    
    // Find seats by seat number (across all flights)
    List<Seat> findBySeatNumber(String seatNumber);
    
    // Find available seats
    List<Seat> findByIsAvailableTrue();
    
    // Find occupied seats
    List<Seat> findByIsAvailableFalse();
    
    // Check if seat exists by class flight ID and seat number
    boolean existsByClassFlightIdAndSeatNumber(Integer classFlightId, String seatNumber);
    
    // Check if seat is available
    @Query("SELECT s.isAvailable FROM Seat s WHERE s.id = :id")
    Boolean isSeatAvailable(@Param("id") Long id);
    
    // Count seats by class flight ID
    long countByClassFlightId(Integer classFlightId);
    
    // Count available seats by class flight ID
    long countByClassFlightIdAndIsAvailableTrue(Integer classFlightId);
    
    // Count occupied seats by class flight ID
    long countByClassFlightIdAndIsAvailableFalse(Integer classFlightId);
    
    // Assign seat to passenger
    @Modifying
    @Query("UPDATE Seat s SET s.passengerId = :passengerId, s.isAvailable = false WHERE s.id = :seatId AND s.isAvailable = true")
    int assignSeatToPassenger(@Param("seatId") Long seatId, @Param("passengerId") UUID passengerId);
    
    // Release seat (make it available again)
    @Modifying
    @Query("UPDATE Seat s SET s.passengerId = null, s.isAvailable = true WHERE s.id = :seatId")
    int releaseSeat(@Param("seatId") Long seatId);
    
    // Release seats by passenger ID
    @Modifying
    @Query("UPDATE Seat s SET s.passengerId = null, s.isAvailable = true WHERE s.passengerId = :passengerId")
    int releaseSeatsByPassenger(@Param("passengerId") UUID passengerId);
    
    // Find seats ordered by seat number
    List<Seat> findByClassFlightIdOrderBySeatNumberAsc(Integer classFlightId);
    
    // Find available seats ordered by seat number
    List<Seat> findByClassFlightIdAndIsAvailableTrueOrderBySeatNumberAsc(Integer classFlightId);
    
    // Find seats by flight ID (through class flight)
    @Query("SELECT s FROM Seat s JOIN s.classFlight cf WHERE cf.flightId = :flightId")
    List<Seat> findByFlightId(@Param("flightId") String flightId);
    
    // Find available seats by flight ID
    @Query("SELECT s FROM Seat s JOIN s.classFlight cf WHERE cf.flightId = :flightId AND s.isAvailable = true ORDER BY s.seatNumber ASC")
    List<Seat> findAvailableSeatsByFlightId(@Param("flightId") String flightId);
    
    // Find occupied seats by flight ID
    @Query("SELECT s FROM Seat s JOIN s.classFlight cf WHERE cf.flightId = :flightId AND s.isAvailable = false")
    List<Seat> findOccupiedSeatsByFlightId(@Param("flightId") String flightId);
    
    // Get seat occupancy rate by class flight
    @Query("SELECT (COUNT(CASE WHEN s.isAvailable = false THEN 1 END) * 100.0 / COUNT(s)) FROM Seat s WHERE s.classFlightId = :classFlightId")
    Double getSeatOccupancyRateByClassFlight(@Param("classFlightId") Integer classFlightId);
    
    // Find seats by seat number pattern (e.g., all 'A' seats)
    @Query("SELECT s FROM Seat s WHERE s.seatNumber LIKE %:pattern%")
    List<Seat> findBySeatNumberPattern(@Param("pattern") String pattern);
    
    // Find window seats (assuming seat letters A and F are windows in typical config)
    @Query("SELECT s FROM Seat s WHERE s.classFlightId = :classFlightId AND (s.seatNumber LIKE '%A' OR s.seatNumber LIKE '%F') AND s.isAvailable = true")
    List<Seat> findAvailableWindowSeats(@Param("classFlightId") Integer classFlightId);
    
    // Find aisle seats (assuming seat letters C and D are aisles in typical config)
    @Query("SELECT s FROM Seat s WHERE s.classFlightId = :classFlightId AND (s.seatNumber LIKE '%C' OR s.seatNumber LIKE '%D') AND s.isAvailable = true")
    List<Seat> findAvailableAisleSeats(@Param("classFlightId") Integer classFlightId);
    
    // Delete seats by class flight ID
    void deleteByClassFlightId(Integer classFlightId);
}
