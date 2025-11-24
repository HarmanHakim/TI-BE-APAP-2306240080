package io.harman.flight_be.repository.flight;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import io.harman.flight_be.model.flight.Flight;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface FlightRepository extends JpaRepository<Flight, String> {
    
    // Find flights by airline ID
    List<Flight> findByAirlineId(String airlineId);
    
    // Find flights by airline ID (not deleted)
    List<Flight> findByAirlineIdAndIsDeletedFalse(String airlineId);
    
    // Find flights by airplane ID
    List<Flight> findByAirplaneId(String airplaneId);
    
    // Find flights by airplane ID (not deleted)
    List<Flight> findByAirplaneIdAndIsDeletedFalse(String airplaneId);
    
    // Find flights by origin airport
    List<Flight> findByOriginAirportCode(String originAirportCode);
    
    // Find flights by origin airport (not deleted)
    List<Flight> findByOriginAirportCodeAndIsDeletedFalse(String originAirportCode);
    
    // Find flights by destination airport
    List<Flight> findByDestinationAirportCode(String destinationAirportCode);
    
    // Find flights by destination airport (not deleted)
    List<Flight> findByDestinationAirportCodeAndIsDeletedFalse(String destinationAirportCode);
    
    // Find flights by route (origin and destination)
    List<Flight> findByOriginAirportCodeAndDestinationAirportCode(String origin, String destination);
    
    // Find flights by route (not deleted)
    List<Flight> findByOriginAirportCodeAndDestinationAirportCodeAndIsDeletedFalse(String origin, String destination);
    
    // Find flights by departure time range
    List<Flight> findByDepartureTimeBetween(LocalDateTime start, LocalDateTime end);
    
    // Find flights by departure time range (not deleted)
    List<Flight> findByDepartureTimeBetweenAndIsDeletedFalse(LocalDateTime start, LocalDateTime end);
    
    // Find flights by status
    List<Flight> findByStatus(Integer status);
    
    // Find flights by status (not deleted)
    List<Flight> findByStatusAndIsDeletedFalse(Integer status);
    
    // Find active (not deleted) flights
    List<Flight> findByIsDeletedFalse();
    
    // Find cancelled flights
    List<Flight> findByStatusAndIsDeletedTrue(Integer status);
    
    // Find flight by ID (not deleted)
    Optional<Flight> findByIdAndIsDeletedFalse(String id);
    
    // Find upcoming flights (scheduled status, future departure)
    @Query("SELECT f FROM Flight f WHERE f.status = 1 AND f.departureTime > :now AND f.isDeleted = false ORDER BY f.departureTime ASC")
    List<Flight> findUpcomingFlights(@Param("now") LocalDateTime now);
    
    // Find upcoming flights by route
    @Query("SELECT f FROM Flight f WHERE f.originAirportCode = :origin AND f.destinationAirportCode = :destination AND f.departureTime > :now AND f.isDeleted = false ORDER BY f.departureTime ASC")
    List<Flight> findUpcomingFlightsByRoute(@Param("origin") String origin, @Param("destination") String destination, @Param("now") LocalDateTime now);
    
    // Find upcoming flights by airline
    @Query("SELECT f FROM Flight f WHERE f.airlineId = :airlineId AND f.departureTime > :now AND f.isDeleted = false ORDER BY f.departureTime ASC")
    List<Flight> findUpcomingFlightsByAirline(@Param("airlineId") String airlineId, @Param("now") LocalDateTime now);
    
    // Find flights departing today
    @Query("SELECT f FROM Flight f WHERE DATE(f.departureTime) = DATE(:date) AND f.isDeleted = false ORDER BY f.departureTime ASC")
    List<Flight> findFlightsDepartingOnDate(@Param("date") LocalDateTime date);
    
    // Find delayed flights
    @Query("SELECT f FROM Flight f WHERE f.status = 4 AND f.isDeleted = false ORDER BY f.departureTime ASC")
    List<Flight> findDelayedFlights();
    
    // Find in-flight flights
    @Query("SELECT f FROM Flight f WHERE f.status = 2 AND f.isDeleted = false ORDER BY f.departureTime ASC")
    List<Flight> findInFlightFlights();
    
    // Count flights by status
    long countByStatus(Integer status);
    
    // Count flights by status (not deleted)
    long countByStatusAndIsDeletedFalse(Integer status);
    
    // Count flights by airline
    long countByAirlineId(String airlineId);
    
    // Count flights by airline (not deleted)
    long countByAirlineIdAndIsDeletedFalse(String airlineId);
    
    // Find flights by terminal
    List<Flight> findByTerminalAndIsDeletedFalse(String terminal);
    
    // Find flights by gate
    List<Flight> findByGateAndIsDeletedFalse(String gate);
    
    // Search flights (route, date, status)
    @Query("SELECT f FROM Flight f WHERE f.originAirportCode = :origin AND f.destinationAirportCode = :destination AND DATE(f.departureTime) = DATE(:date) AND f.status = 1 AND f.isDeleted = false ORDER BY f.departureTime ASC")
    List<Flight> searchFlights(@Param("origin") String origin, @Param("destination") String destination, @Param("date") LocalDateTime date);
    
    // Get distinct origin airports
    @Query("SELECT DISTINCT f.originAirportCode FROM Flight f WHERE f.isDeleted = false ORDER BY f.originAirportCode")
    List<String> findDistinctOriginAirports();
    
    // Get distinct destination airports
    @Query("SELECT DISTINCT f.destinationAirportCode FROM Flight f WHERE f.isDeleted = false ORDER BY f.destinationAirportCode")
    List<String> findDistinctDestinationAirports();
    
    // Find flights with available seats
    @Query("SELECT DISTINCT f FROM Flight f JOIN f.classes cf WHERE cf.availableSeats > 0 AND f.isDeleted = false AND f.status = 1 ORDER BY f.departureTime ASC")
    List<Flight> findFlightsWithAvailableSeats();
    
    // Find flights by route with available seats
    @Query("SELECT DISTINCT f FROM Flight f JOIN f.classes cf WHERE f.originAirportCode = :origin AND f.destinationAirportCode = :destination AND cf.availableSeats > 0 AND f.isDeleted = false AND f.status = 1 ORDER BY f.departureTime ASC")
    List<Flight> findFlightsByRouteWithAvailableSeats(@Param("origin") String origin, @Param("destination") String destination);
}
