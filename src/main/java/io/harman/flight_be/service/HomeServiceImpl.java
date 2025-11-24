package io.harman.flight_be.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.harman.flight_be.repository.flight.AirlineRepository;
import io.harman.flight_be.repository.flight.BookingRepository;
import io.harman.flight_be.repository.flight.FlightRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
@Transactional(readOnly = true)
public class HomeServiceImpl implements HomeService {

    private final FlightRepository flightRepository;
    private final BookingRepository bookingRepository;
    private final AirlineRepository airlineRepository;

    public HomeServiceImpl(FlightRepository flightRepository,
                          BookingRepository bookingRepository,
                          AirlineRepository airlineRepository) {
        this.flightRepository = flightRepository;
        this.bookingRepository = bookingRepository;
        this.airlineRepository = airlineRepository;
    }

    @Override
    public Map<String, Object> getHomeStatistics() {
        Map<String, Object> statistics = new HashMap<>();

        // 1. Number of active flights today
        LocalDateTime today = LocalDate.now().atStartOfDay();
        long activeFlightsToday = flightRepository.findFlightsDepartingOnDate(today).stream()
                .filter(flight -> !flight.getIsDeleted() && flight.getStatus() != 5) // Not deleted and not cancelled
                .count();

        // 2. Number of bookings created today
        LocalDateTime startOfDay = today;
        LocalDateTime endOfDay = today.plusDays(1).minusSeconds(1);
        long bookingsCreatedToday = bookingRepository.findByCreatedAtBetweenAndIsDeletedFalse(startOfDay, endOfDay).size();

        // 3. Number of registered airlines
        long totalAirlines = airlineRepository.count();

        statistics.put("activeFlightsToday", activeFlightsToday);
        statistics.put("bookingsCreatedToday", bookingsCreatedToday);
        statistics.put("totalRegisteredAirlines", totalAirlines);
        statistics.put("date", LocalDate.now());

        return statistics;
    }
}
