package io.harman.flight_be.repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import io.harman.flight_be.model.Booking;

public interface BookingRepository extends JpaRepository<Booking, String> {
    
    // Find bookings by flight ID
    List<Booking> findByFlightId(String flightId);
    
    // Find bookings by flight ID (not deleted)
    List<Booking> findByFlightIdAndIsDeletedFalse(String flightId);
    
    // Find bookings by class flight ID
    List<Booking> findByClassFlightId(Integer classFlightId);
    
    // Find bookings by status
    List<Booking> findByStatus(Integer status);
    
    // Find bookings by status (not deleted)
    List<Booking> findByStatusAndIsDeletedFalse(Integer status);
    
    // Find bookings by contact email
    List<Booking> findByContactEmail(String contactEmail);
    
    // Find bookings by contact email (not deleted)
    List<Booking> findByContactEmailAndIsDeletedFalse(String contactEmail);
    
    // Find bookings by contact phone
    List<Booking> findByContactPhone(String contactPhone);
    
    // Find bookings by created date range
    List<Booking> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
    
    // Find bookings by created date range (not deleted)
    List<Booking> findByCreatedAtBetweenAndIsDeletedFalse(LocalDateTime start, LocalDateTime end);
    
    // Find active (not deleted) bookings
    List<Booking> findByIsDeletedFalse();
    
    // Find cancelled bookings
    List<Booking> findByStatusAndIsDeletedTrue(Integer status);
    
    // Find booking by ID (not deleted)
    Optional<Booking> findByIdAndIsDeletedFalse(String id);
    
    // Count bookings by flight
    long countByFlightId(String flightId);
    
    // Count bookings by flight (not deleted)
    long countByFlightIdAndIsDeletedFalse(String flightId);
    
    // Count bookings by status
    long countByStatus(Integer status);
    
    // Count bookings by status (not deleted)
    long countByStatusAndIsDeletedFalse(Integer status);
    
    // Get total revenue by flight
    @Query("SELECT SUM(b.totalPrice) FROM Booking b WHERE b.flightId = :flightId AND b.isDeleted = false AND b.status = 2")
    BigDecimal getTotalRevenueByFlight(@Param("flightId") String flightId);
    
    // Get total revenue by date range
    @Query("SELECT SUM(b.totalPrice) FROM Booking b WHERE b.createdAt BETWEEN :start AND :end AND b.isDeleted = false AND b.status = 2")
    BigDecimal getTotalRevenueByDateRange(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
    
    // Get total passengers by flight
    @Query("SELECT SUM(b.passengerCount) FROM Booking b WHERE b.flightId = :flightId AND b.isDeleted = false AND b.status != 3")
    Integer getTotalPassengersByFlight(@Param("flightId") String flightId);
    
    // Find unpaid bookings older than specified date
    @Query("SELECT b FROM Booking b WHERE b.status = 1 AND b.createdAt < :date AND b.isDeleted = false")
    List<Booking> findUnpaidBookingsOlderThan(@Param("date") LocalDateTime date);
    
    // Find paid bookings by flight
    @Query("SELECT b FROM Booking b WHERE b.flightId = :flightId AND b.status = 2 AND b.isDeleted = false")
    List<Booking> findPaidBookingsByFlight(@Param("flightId") String flightId);
    
    // Get bookings ordered by creation date (newest first)
    List<Booking> findAllByOrderByCreatedAtDesc();
    
    // Find bookings by email and status
    List<Booking> findByContactEmailAndStatusAndIsDeletedFalse(String email, Integer status);
    
    // Find bookings by total price range
    List<Booking> findByTotalPriceBetweenAndIsDeletedFalse(BigDecimal minPrice, BigDecimal maxPrice);
}
