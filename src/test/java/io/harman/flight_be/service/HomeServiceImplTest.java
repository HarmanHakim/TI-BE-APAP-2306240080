package io.harman.flight_be.service;

import io.harman.flight_be.model.flight.Booking;
import io.harman.flight_be.model.flight.Flight;
import io.harman.flight_be.repository.flight.AirlineRepository;
import io.harman.flight_be.repository.flight.BookingRepository;
import io.harman.flight_be.repository.flight.FlightRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class HomeServiceImplTest {

    @Mock
    private FlightRepository flightRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private AirlineRepository airlineRepository;

    @InjectMocks
    private HomeServiceImpl homeService;

    private LocalDateTime todayStart;

    @BeforeEach
    void setUp() {
        todayStart = LocalDate.now().atStartOfDay();
    }

    @Test
    void testGetHomeStatistics() {
        // Prepare flights: one active, one deleted, one cancelled
        Flight active = Flight.builder().id("F1").isDeleted(false).status(1).build();
        Flight deleted = Flight.builder().id("F2").isDeleted(true).status(1).build();
        Flight cancelled = Flight.builder().id("F3").isDeleted(false).status(5).build();

        List<Flight> flights = Arrays.asList(active, deleted, cancelled);

        when(flightRepository.findFlightsDepartingOnDate(any(LocalDateTime.class))).thenReturn(flights);

        // Prepare bookings created today
        Booking b1 = Booking.builder().id("B1").build();
        Booking b2 = Booking.builder().id("B2").build();
        when(bookingRepository.findByCreatedAtBetweenAndIsDeletedFalse(eq(todayStart),
                eq(todayStart.plusDays(1).minusSeconds(1))))
                .thenReturn(Arrays.asList(b1, b2));

        // Airlines count
        when(airlineRepository.count()).thenReturn(3L);

        Map<String, Object> stats = homeService.getHomeStatistics();

        assertNotNull(stats);
        assertEquals(1L, ((Number) stats.get("activeFlightsToday")).longValue());
        assertEquals(2, ((Number) stats.get("bookingsCreatedToday")).intValue());
        assertEquals(3L, ((Number) stats.get("totalRegisteredAirlines")).longValue());
        assertEquals(LocalDate.now(), stats.get("date"));
    }

    @Test
    void testGetHomeStatisticsWhenNoData() {
        when(flightRepository.findFlightsDepartingOnDate(any(LocalDateTime.class))).thenReturn(Arrays.asList());
        when(bookingRepository.findByCreatedAtBetweenAndIsDeletedFalse(eq(todayStart),
                eq(todayStart.plusDays(1).minusSeconds(1))))
                .thenReturn(Arrays.asList());
        when(airlineRepository.count()).thenReturn(0L);

        Map<String, Object> stats = homeService.getHomeStatistics();

        assertNotNull(stats);
        assertEquals(0L, ((Number) stats.get("activeFlightsToday")).longValue());
        assertEquals(0, ((Number) stats.get("bookingsCreatedToday")).intValue());
        assertEquals(0L, ((Number) stats.get("totalRegisteredAirlines")).longValue());
    }
}
