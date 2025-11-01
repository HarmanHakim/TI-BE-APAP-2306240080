package io.harman.flight_be.repository;

import io.harman.flight_be.model.BookingPassenger;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BookingPassengerRepository extends JpaRepository<BookingPassenger, BookingPassenger.BookingPassengerId> {
    
    // Find all booking-passengers by booking ID
    List<BookingPassenger> findByBookingId(String bookingId);
    
    // Find all booking-passengers by passenger ID
    List<BookingPassenger> findByPassengerId(UUID passengerId);
    
    // Find specific booking-passenger
    Optional<BookingPassenger> findByBookingIdAndPassengerId(String bookingId, UUID passengerId);
    
    // Check if booking-passenger exists
    boolean existsByBookingIdAndPassengerId(String bookingId, UUID passengerId);
    
    // Count passengers by booking
    long countByBookingId(String bookingId);
    
    // Count bookings by passenger
    long countByPassengerId(UUID passengerId);
    
    // Delete by booking ID and passenger ID
    void deleteByBookingIdAndPassengerId(String bookingId, UUID passengerId);
    
    // Delete all by booking ID
    void deleteByBookingId(String bookingId);
    
    // Delete all by passenger ID
    void deleteByPassengerId(UUID passengerId);
    
    // Get all passengers for a specific booking with details
    @Query("SELECT bp FROM BookingPassenger bp JOIN FETCH bp.passengerId WHERE bp.bookingId = :bookingId")
    List<BookingPassenger> findByBookingIdWithPassengerDetails(@Param("bookingId") String bookingId);
    
    // Get all bookings for a specific passenger
    @Query("SELECT bp FROM BookingPassenger bp WHERE bp.passengerId = :passengerId ORDER BY bp.createdAt DESC")
    List<BookingPassenger> findByPassengerIdOrderByCreatedAtDesc(@Param("passengerId") UUID passengerId);
    
    // Find booking-passengers created in date range
    @Query("SELECT bp FROM BookingPassenger bp WHERE bp.createdAt BETWEEN :start AND :end")
    List<BookingPassenger> findByCreatedAtBetween(@Param("start") java.time.LocalDateTime start, @Param("end") java.time.LocalDateTime end);
}
