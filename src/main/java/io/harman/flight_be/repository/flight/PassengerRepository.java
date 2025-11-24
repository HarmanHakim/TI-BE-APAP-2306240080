package io.harman.flight_be.repository.flight;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import io.harman.flight_be.model.flight.Passenger;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PassengerRepository extends JpaRepository<Passenger, UUID> {
    
    // Find passengers by full name (case insensitive, partial match)
    List<Passenger> findByFullNameContainingIgnoreCase(String fullName);
    
    // Find passenger by full name (exact match)
    Optional<Passenger> findByFullName(String fullName);
    
    // Find passenger by ID/Passport
    Optional<Passenger> findByIdPassport(String idPassport);
    
    // Find passengers by ID/Passport (partial match)
    List<Passenger> findByIdPassportContaining(String idPassport);
    
    // Find passengers by gender
    List<Passenger> findByGender(Integer gender);
    
    // Find passengers by birth date
    List<Passenger> findByBirthDate(LocalDate birthDate);
    
    // Find passengers by birth date range
    List<Passenger> findByBirthDateBetween(LocalDate startDate, LocalDate endDate);
    
    // Find passengers by age range (calculated from birth date)
    @Query("SELECT p FROM Passenger p WHERE YEAR(CURRENT_DATE) - YEAR(p.birthDate) BETWEEN :minAge AND :maxAge")
    List<Passenger> findByAgeRange(@Param("minAge") Integer minAge, @Param("maxAge") Integer maxAge);
    
    // Find adult passengers (age >= 18)
    @Query("SELECT p FROM Passenger p WHERE YEAR(CURRENT_DATE) - YEAR(p.birthDate) >= 18")
    List<Passenger> findAdultPassengers();
    
    // Find child passengers (age < 18)
    @Query("SELECT p FROM Passenger p WHERE YEAR(CURRENT_DATE) - YEAR(p.birthDate) < 18")
    List<Passenger> findChildPassengers();
    
    // Check if passenger exists by ID/Passport
    boolean existsByIdPassport(String idPassport);
    
    // Check if passenger exists by full name
    boolean existsByFullName(String fullName);
    
    // Count passengers by gender
    long countByGender(Integer gender);
    
    // Get all passengers ordered by full name
    List<Passenger> findAllByOrderByFullNameAsc();
    
    // Find passengers created in date range
    List<Passenger> findByCreatedAtBetween(java.time.LocalDateTime start, java.time.LocalDateTime end);
    
    // Find recently created passengers
    @Query("SELECT p FROM Passenger p ORDER BY p.createdAt DESC")
    List<Passenger> findRecentlyCreatedPassengers();
    
    // Search passengers by multiple criteria
    @Query("SELECT p FROM Passenger p WHERE " +
           "(:fullName IS NULL OR LOWER(p.fullName) LIKE LOWER(CONCAT('%', :fullName, '%'))) AND " +
           "(:idPassport IS NULL OR p.idPassport LIKE CONCAT('%', :idPassport, '%')) AND " +
           "(:gender IS NULL OR p.gender = :gender)")
    List<Passenger> searchPassengers(
        @Param("fullName") String fullName,
        @Param("idPassport") String idPassport,
        @Param("gender") Integer gender
    );
    
    // Get passenger statistics by gender
    @Query("SELECT p.gender, COUNT(p) FROM Passenger p GROUP BY p.gender")
    List<Object[]> getPassengerStatisticsByGender();
    
    // Find passengers with bookings
    @Query("SELECT DISTINCT p FROM Passenger p JOIN p.bookings b WHERE b.isDeleted = false")
    List<Passenger> findPassengersWithBookings();
    
    // Find passengers by birth year
    @Query("SELECT p FROM Passenger p WHERE YEAR(p.birthDate) = :year")
    List<Passenger> findByBirthYear(@Param("year") Integer year);
}
