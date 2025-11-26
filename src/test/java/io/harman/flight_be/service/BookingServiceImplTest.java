package io.harman.flight_be.service;

import io.harman.flight_be.dto.booking.CreateBookingDto;
import io.harman.flight_be.dto.booking.ReadBookingDto;
import io.harman.flight_be.dto.booking.UpdateBookingDto;
import io.harman.flight_be.model.flight.Booking;
import io.harman.flight_be.model.flight.ClassFlight;
import io.harman.flight_be.model.flight.Flight;
import io.harman.flight_be.model.flight.Passenger;
import io.harman.flight_be.model.flight.Seat;
import io.harman.flight_be.repository.flight.BookingRepository;
import io.harman.flight_be.repository.flight.ClassFlightRepository;
import io.harman.flight_be.repository.flight.FlightRepository;
import io.harman.flight_be.repository.flight.PassengerRepository;
import io.harman.flight_be.repository.flight.SeatRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class BookingServiceImplTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private FlightRepository flightRepository;

    @Mock
    private ClassFlightRepository classFlightRepository;

    @Mock
    private PassengerRepository passengerRepository;

    @Mock
    private SeatRepository seatRepository;

    @InjectMocks
    private BookingServiceImpl bookingService;

    private Booking booking1;
    private Booking booking2;
    private Flight flight;
    private ClassFlight classFlight;
    private Passenger passenger1;
    private CreateBookingDto createBookingDto;
    private UpdateBookingDto updateBookingDto;

    @BeforeEach
    void setUp() {
        flight = Flight.builder()
                .id("FL001")
                .airlineId("GA")
                .airplaneId("AP001")
                .originAirportCode("CGK")
                .destinationAirportCode("DPS")
                .departureTime(LocalDateTime.now().plusDays(1))
                .arrivalTime(LocalDateTime.now().plusDays(1).plusHours(2))
                .status(1) // Scheduled
                .isDeleted(false)
                .build();

        classFlight = ClassFlight.builder()
                .id(1)
                .classType("Economy")
                .build();

        passenger1 = Passenger.builder()
                .id(UUID.randomUUID())
                .fullName("John Doe")
                .birthDate(LocalDate.of(1990, 1, 1))
                .gender(1)
                .idPassport("A1234567")
                .build();

        booking1 = Booking.builder()
                .id("BK001")
                .flightId("FL001")
                .classFlightId(1)
                .contactEmail("john@example.com")
                .contactPhone("08123456789")
                .passengerCount(1)
                .status(1) // Unpaid
                .totalPrice(new BigDecimal("1500000"))
                .passengers(Arrays.asList(passenger1))
                .isDeleted(false)
                .createdAt(LocalDateTime.now())
                .build();

        booking2 = Booking.builder()
                .id("BK002")
                .flightId("FL001")
                .classFlightId(1)
                .contactEmail("jane@example.com")
                .contactPhone("08198765432")
                .passengerCount(1)
                .status(2) // Paid
                .totalPrice(new BigDecimal("1500000"))
                .isDeleted(false)
                .createdAt(LocalDateTime.now())
                .build();

        createBookingDto = CreateBookingDto.builder()
                .flightId("FL001")
                .classFlightId(1)
                .contactEmail("test@example.com")
                .contactPhone("08111111111")
                .passengerCount(1)
                .status(1)
                .totalPrice(new BigDecimal("1500000"))
                .passengerIds(Arrays.asList(passenger1.getId()))
                .build();

        updateBookingDto = UpdateBookingDto.builder()
                .id("BK001")
                .flightId("FL001")
                .classFlightId(1)
                .contactEmail("updated@example.com")
                .contactPhone("08222222222")
                .passengerCount(1)
                .status(2)
                .totalPrice(new BigDecimal("1600000"))
                .build();
    }

    @Test
    void testGetAllBookings() {
        List<Booking> bookings = Arrays.asList(booking1, booking2);
        when(bookingRepository.findAllByOrderByCreatedAtDesc()).thenReturn(bookings);

        List<ReadBookingDto> result = bookingService.getAllBookings();

        assertEquals(2, result.size());
        verify(bookingRepository).findAllByOrderByCreatedAtDesc();
    }

    @Test
    void testGetAllActiveBookings() {
        List<Booking> bookings = Arrays.asList(booking1, booking2);
        when(bookingRepository.findByIsDeletedFalse()).thenReturn(bookings);

        List<ReadBookingDto> result = bookingService.getAllActiveBookings();

        assertEquals(2, result.size());
        verify(bookingRepository).findByIsDeletedFalse();
    }

    @Test
    void testGetBookingByIdSuccess() {
        when(bookingRepository.findByIdWithPassengers("BK001")).thenReturn(Optional.of(booking1));

        ReadBookingDto result = bookingService.getBookingById("BK001");

        assertNotNull(result);
        assertEquals("BK001", result.getId());
        verify(bookingRepository).findByIdWithPassengers("BK001");
    }

    @Test
    void testGetBookingByIdNotFound() {
        when(bookingRepository.findByIdWithPassengers("XX")).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> bookingService.getBookingById("XX"));

        assertTrue(exception.getMessage().contains("not found"));
        verify(bookingRepository).findByIdWithPassengers("XX");
    }

    @Test
    void testCreateBookingSuccess() {
        // Create a mock seat for assignment
        Seat mockSeat = Seat.builder()
                .id(1L)
                .classFlightId(1)
                .seatNumber("1A")
                .isAvailable(true)
                .build();

        when(flightRepository.findByIdAndIsDeletedFalse("FL001")).thenReturn(Optional.of(flight));
        when(classFlightRepository.findById(1)).thenReturn(Optional.of(classFlight));
        when(passengerRepository.findById(passenger1.getId())).thenReturn(Optional.of(passenger1));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking1);
        when(bookingRepository.findByIdWithPassengers(anyString())).thenReturn(Optional.of(booking1));
        // Mock available seats for assignment
        when(seatRepository.findByClassFlightIdAndIsAvailableTrueOrderBySeatNumberAsc(1))
                .thenReturn(Arrays.asList(mockSeat));
        // Mock successful seat assignment
        when(seatRepository.assignSeatToPassenger(1L, passenger1.getId())).thenReturn(1);

        ReadBookingDto result = bookingService.createBooking(createBookingDto);

        assertNotNull(result);
        verify(flightRepository).findByIdAndIsDeletedFalse("FL001");
        verify(classFlightRepository).findById(1);
        verify(passengerRepository).findById(passenger1.getId());
        verify(bookingRepository).save(any(Booking.class));
        verify(seatRepository).assignSeatToPassenger(1L, passenger1.getId());
    }

    @Test
    void testCreateBookingFlightNotFound() {
        when(flightRepository.findByIdAndIsDeletedFalse("FL001")).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> bookingService.createBooking(createBookingDto));

        assertTrue(exception.getMessage().contains("not found"));
        verify(flightRepository).findByIdAndIsDeletedFalse("FL001");
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void testCreateBookingFlightNotScheduled() {
        flight.setStatus(2); // Departed
        when(flightRepository.findByIdAndIsDeletedFalse("FL001")).thenReturn(Optional.of(flight));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> bookingService.createBooking(createBookingDto));

        assertTrue(exception.getMessage().contains("Scheduled"));
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void testCreateBookingClassFlightNotFound() {
        when(flightRepository.findByIdAndIsDeletedFalse("FL001")).thenReturn(Optional.of(flight));
        when(classFlightRepository.findById(1)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> bookingService.createBooking(createBookingDto));

        assertTrue(exception.getMessage().contains("not found"));
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void testCreateBookingTooManyPassengers() {
        createBookingDto.setPassengerCount(11);
        // Add required mocks for flight and classFlight existence
        when(flightRepository.findByIdAndIsDeletedFalse("FL001")).thenReturn(Optional.of(flight));
        when(classFlightRepository.findById(1)).thenReturn(Optional.of(classFlight));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> bookingService.createBooking(createBookingDto));

        assertEquals("Maximum 10 passengers allowed per booking", exception.getMessage());
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void testUpdateBookingSuccess() {
        when(bookingRepository.findById("BK001")).thenReturn(Optional.of(booking1));
        when(flightRepository.findById("FL001")).thenReturn(Optional.of(flight));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking1);

        ReadBookingDto result = bookingService.updateBooking(updateBookingDto);

        assertNotNull(result);
        verify(bookingRepository, times(2)).findById("BK001");
        verify(bookingRepository).save(any(Booking.class));
    }

    @Test
    void testUpdateBookingNotFound() {
        when(bookingRepository.findById("XX")).thenReturn(Optional.empty());
        updateBookingDto.setId("XX");

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> bookingService.updateBooking(updateBookingDto));

        assertTrue(exception.getMessage().contains("not found"));
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void testDeleteBookingSuccess() {
        when(bookingRepository.findById("BK001")).thenReturn(Optional.of(booking1));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking1);

        assertDoesNotThrow(() -> bookingService.deleteBooking("BK001"));

        verify(bookingRepository, times(2)).findById("BK001");
        verify(bookingRepository).save(any(Booking.class));
    }

    @Test
    void testDeleteBookingNotFound() {
        when(bookingRepository.findById("XX")).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> bookingService.deleteBooking("XX"));

        assertTrue(exception.getMessage().contains("not found"));
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void testGetBookingsByStatus() {
        List<Booking> bookings = Arrays.asList(booking1);
        when(bookingRepository.findByStatusAndIsDeletedFalse(1)).thenReturn(bookings);

        List<ReadBookingDto> result = bookingService.getBookingsByStatus(1);

        assertEquals(1, result.size());
        verify(bookingRepository).findByStatusAndIsDeletedFalse(1);
    }

    @Test
    void testGetBookingsByEmail() {
        List<Booking> bookings = Arrays.asList(booking1);
        when(bookingRepository.findByContactEmailAndIsDeletedFalse("john@example.com")).thenReturn(bookings);

        List<ReadBookingDto> result = bookingService.getBookingsByEmail("john@example.com");

        assertEquals(1, result.size());
        verify(bookingRepository).findByContactEmailAndIsDeletedFalse("john@example.com");
    }

    @Test
    void testGetBookingsByFlightId() {
        List<Booking> bookings = Arrays.asList(booking1, booking2);
        when(bookingRepository.findByFlightIdAndIsDeletedFalse("FL001")).thenReturn(bookings);

        List<ReadBookingDto> result = bookingService.getBookingsByFlightId("FL001");

        assertEquals(2, result.size());
        verify(bookingRepository).findByFlightIdAndIsDeletedFalse("FL001");
    }

    @Test
    void testGetBookingsByDateRange() {
        LocalDateTime start = LocalDateTime.now().minusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(1);
        List<Booking> bookings = Arrays.asList(booking1, booking2);
        when(bookingRepository.findByCreatedAtBetweenAndIsDeletedFalse(start, end)).thenReturn(bookings);

        List<ReadBookingDto> result = bookingService.getBookingsByDateRange(start, end);

        assertEquals(2, result.size());
        verify(bookingRepository).findByCreatedAtBetweenAndIsDeletedFalse(start, end);
    }

    @Test
    void testGetBookingStatistics() {
        LocalDateTime start = LocalDateTime.now().minusDays(7);
        LocalDateTime end = LocalDateTime.now().plusDays(7);

        Booking statBooking1 = Booking.builder()
                .id("BS001")
                .flightId("FL001")
                .classFlightId(1)
                .status(1)
                .totalPrice(new BigDecimal("100"))
                .isDeleted(false)
                .createdAt(LocalDateTime.now())
                .build();

        Booking statBooking2 = Booking.builder()
                .id("BS002")
                .flightId("FL001")
                .classFlightId(1)
                .status(2)
                .totalPrice(new BigDecimal("200"))
                .isDeleted(false)
                .createdAt(LocalDateTime.now())
                .build();

        List<Booking> bookings = Arrays.asList(statBooking1, statBooking2);

        when(bookingRepository.findByCreatedAtBetweenAndIsDeletedFalse(start, end)).thenReturn(bookings);
        when(flightRepository.findById("FL001")).thenReturn(Optional.of(flight));

        Map<String, Object> result = bookingService.getBookingStatistics(start, end);

        // totalBookings should be 2 (both statuses 1 and 2 are counted)
        assertEquals(2, ((Integer) result.get("totalBookings")).intValue());

        // overall revenue should be 100 + 200 = 300
        assertEquals(new BigDecimal("300"), (BigDecimal) result.get("potentialRevenue"));

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> flightStats = (List<Map<String, Object>>) result.get("flightStats");
        assertEquals(1, flightStats.size());

        Map<String, Object> stat = flightStats.get(0);
        assertEquals("FL001", stat.get("flightId"));
        assertEquals(2, ((Integer) stat.get("bookingCount")).intValue());
        assertEquals(new BigDecimal("300"), (BigDecimal) stat.get("potentialRevenue"));
    }
}
