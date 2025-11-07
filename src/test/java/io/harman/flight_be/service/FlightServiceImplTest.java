package io.harman.flight_be.service;

import io.harman.flight_be.dto.flight.CreateFlightDto;
import io.harman.flight_be.dto.flight.ReadFlightDto;
import io.harman.flight_be.dto.flight.UpdateFlightDto;
import io.harman.flight_be.model.Airline;
import io.harman.flight_be.model.Airplane;
import io.harman.flight_be.model.Flight;
import io.harman.flight_be.repository.AirlineRepository;
import io.harman.flight_be.repository.AirplaneRepository;
import io.harman.flight_be.repository.BookingRepository;
import io.harman.flight_be.repository.FlightRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class FlightServiceImplTest {

    @Mock
    private FlightRepository flightRepository;

    @Mock
    private AirlineRepository airlineRepository;

    @Mock
    private AirplaneRepository airplaneRepository;

    @Mock
    private BookingRepository bookingRepository;

    @InjectMocks
    private FlightServiceImpl flightService;

    private Flight flight1;
    private Flight flight2;
    private Airline airline;
    private Airplane airplane;
    private CreateFlightDto createFlightDto;
    private UpdateFlightDto updateFlightDto;

    @BeforeEach
    void setUp() {
        airline = Airline.builder()
                .id("GA")
                .name("Garuda Indonesia")
                .country("Indonesia")
                .build();

        airplane = Airplane.builder()
                .id("AP001")
                .airlineId("GA")
                .model("Boeing 737")
                .seatCapacity(180)
                .isDeleted(false)
                .build();

        flight1 = Flight.builder()
                .id("FL001")
                .airlineId("GA")
                .airplaneId("AP001")
                .originAirportCode("CGK")
                .destinationAirportCode("DPS")
                .departureTime(LocalDateTime.now().plusDays(1))
                .arrivalTime(LocalDateTime.now().plusDays(1).plusHours(2))
                .terminal("Terminal 1")
                .gate("A1")
                .baggageAllowance(20)
                .facilities("WiFi, Meal")
                .status(1) // Scheduled
                .isDeleted(false)
                .build();

        flight2 = Flight.builder()
                .id("FL002")
                .airlineId("GA")
                .airplaneId("AP001")
                .originAirportCode("DPS")
                .destinationAirportCode("CGK")
                .departureTime(LocalDateTime.now().plusDays(2))
                .arrivalTime(LocalDateTime.now().plusDays(2).plusHours(2))
                .terminal("Terminal 2")
                .gate("B2")
                .baggageAllowance(20)
                .facilities("WiFi")
                .status(1)
                .isDeleted(false)
                .build();

        createFlightDto = CreateFlightDto.builder()
                .airlineId("GA")
                .airplaneId("AP001")
                .originAirportCode("CGK")
                .destinationAirportCode("DPS")
                .departureTime(LocalDateTime.now().plusDays(1))
                .arrivalTime(LocalDateTime.now().plusDays(1).plusHours(2))
                .terminal("Terminal 1")
                .gate("A1")
                .baggageAllowance(20)
                .facilities("WiFi")
                .status(1)
                .build();

        updateFlightDto = UpdateFlightDto.builder()
                .id("FL001")
                .airlineId("GA")
                .airplaneId("AP001")
                .originAirportCode("CGK")
                .destinationAirportCode("DPS")
                .departureTime(LocalDateTime.now().plusDays(1))
                .arrivalTime(LocalDateTime.now().plusDays(1).plusHours(3))
                .terminal("Terminal 1")
                .gate("A2")
                .baggageAllowance(25)
                .facilities("WiFi, Meal")
                .status(1)
                .build();
    }

    @Test
    void testGetAllFlights() {
        List<Flight> flights = Arrays.asList(flight1, flight2);
        when(flightRepository.findAll()).thenReturn(flights);

        List<ReadFlightDto> result = flightService.getAllFlights();

        assertEquals(2, result.size());
        verify(flightRepository).findAll();
    }

    @Test
    void testGetAllActiveFlights() {
        List<Flight> flights = Arrays.asList(flight1, flight2);
        when(flightRepository.findByIsDeletedFalse()).thenReturn(flights);

        List<ReadFlightDto> result = flightService.getAllActiveFlights();

        assertEquals(2, result.size());
        verify(flightRepository).findByIsDeletedFalse();
    }

    @Test
    void testGetFlightByIdSuccess() {
        when(flightRepository.findById("FL001")).thenReturn(Optional.of(flight1));

        ReadFlightDto result = flightService.getFlightById("FL001");

        assertNotNull(result);
        assertEquals("FL001", result.getId());
        assertEquals("CGK", result.getOriginAirportCode());
        verify(flightRepository).findById("FL001");
    }

    @Test
    void testGetFlightByIdNotFound() {
        when(flightRepository.findById("XX")).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> flightService.getFlightById("XX"));

        assertTrue(exception.getMessage().contains("not found"));
        verify(flightRepository).findById("XX");
    }

    @Test
    void testCreateFlightSuccess() {
        when(airlineRepository.findById("GA")).thenReturn(Optional.of(airline));
        when(airplaneRepository.findByIdAndIsDeletedFalse("AP001")).thenReturn(Optional.of(airplane));
        when(flightRepository.save(any(Flight.class))).thenReturn(flight1);

        ReadFlightDto result = flightService.createFlight(createFlightDto);

        assertNotNull(result);
        verify(airlineRepository).findById("GA");
        verify(airplaneRepository).findByIdAndIsDeletedFalse("AP001");
        verify(flightRepository).save(any(Flight.class));
    }

    @Test
    void testCreateFlightAirlineNotFound() {
        when(airlineRepository.findById("GA")).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> flightService.createFlight(createFlightDto));

        assertTrue(exception.getMessage().contains("not found"));
        verify(airlineRepository).findById("GA");
        verify(flightRepository, never()).save(any(Flight.class));
    }

    @Test
    void testCreateFlightAirplaneNotFound() {
        when(airlineRepository.findById("GA")).thenReturn(Optional.of(airline));
        when(airplaneRepository.findByIdAndIsDeletedFalse("AP001")).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> flightService.createFlight(createFlightDto));

        assertTrue(exception.getMessage().contains("not found"));
        verify(flightRepository, never()).save(any(Flight.class));
    }

    @Test
    void testCreateFlightInvalidTime() {
        createFlightDto.setDepartureTime(LocalDateTime.now().plusDays(1).plusHours(3));
        createFlightDto.setArrivalTime(LocalDateTime.now().plusDays(1));

        when(airlineRepository.findById("GA")).thenReturn(Optional.of(airline));
        when(airplaneRepository.findByIdAndIsDeletedFalse("AP001")).thenReturn(Optional.of(airplane));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> flightService.createFlight(createFlightDto));

        assertTrue(exception.getMessage().contains("before"));
        verify(flightRepository, never()).save(any(Flight.class));
    }

    @Test
    void testUpdateFlightSuccess() {
        when(flightRepository.findById("FL001")).thenReturn(Optional.of(flight1));
        when(flightRepository.save(any(Flight.class))).thenReturn(flight1);

        ReadFlightDto result = flightService.updateFlight(updateFlightDto);

        assertNotNull(result);
        verify(flightRepository, times(2)).findById("FL001");
        verify(flightRepository).save(any(Flight.class));
    }

    @Test
    void testUpdateFlightNotFound() {
        when(flightRepository.findById("XX")).thenReturn(Optional.empty());
        updateFlightDto.setId("XX");

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> flightService.updateFlight(updateFlightDto));

        assertTrue(exception.getMessage().contains("not found"));
        verify(flightRepository, never()).save(any(Flight.class));
    }

    @Test
    void testDeleteFlightSuccess() {
        when(flightRepository.findById("FL001")).thenReturn(Optional.of(flight1));
        when(flightRepository.save(any(Flight.class))).thenReturn(flight1);

        assertDoesNotThrow(() -> flightService.deleteFlight("FL001"));

        verify(flightRepository, times(2)).findById("FL001");
        verify(flightRepository).save(any(Flight.class));
    }

    @Test
    void testDeleteFlightNotFound() {
        when(flightRepository.findById("XX")).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> flightService.deleteFlight("XX"));

        assertTrue(exception.getMessage().contains("not found"));
        verify(flightRepository, never()).save(any(Flight.class));
    }

    @Test
    void testSearchFlights() {
        List<Flight> flights = Arrays.asList(flight1);
        when(flightRepository.findByIsDeletedFalse()).thenReturn(flights);

        List<ReadFlightDto> result = flightService.searchFlights("CGK", "DPS", null, null, null);

        assertEquals(1, result.size());
        verify(flightRepository).findByIsDeletedFalse();
    }

    @Test
    void testGetFlightsByRoute() {
        List<Flight> flights = Arrays.asList(flight1);
        when(flightRepository.findByOriginAirportCodeAndDestinationAirportCodeAndIsDeletedFalse("CGK", "DPS"))
                .thenReturn(flights);

        List<ReadFlightDto> result = flightService.getFlightsByRoute("CGK", "DPS");

        assertEquals(1, result.size());
        verify(flightRepository).findByOriginAirportCodeAndDestinationAirportCodeAndIsDeletedFalse("CGK", "DPS");
    }

    @Test
    void testGetFlightsByStatus() {
        List<Flight> flights = Arrays.asList(flight1, flight2);
        when(flightRepository.findByStatusAndIsDeletedFalse(1)).thenReturn(flights);

        List<ReadFlightDto> result = flightService.getFlightsByStatus(1);

        assertEquals(2, result.size());
        verify(flightRepository).findByStatusAndIsDeletedFalse(1);
    }

    @Test
    void testGetUpcomingFlights() {
        List<Flight> flights = Arrays.asList(flight1, flight2);
        when(flightRepository.findUpcomingFlights(any(LocalDateTime.class))).thenReturn(flights);

        List<ReadFlightDto> result = flightService.getUpcomingFlights();

        assertEquals(2, result.size());
        verify(flightRepository).findUpcomingFlights(any(LocalDateTime.class));
    }
}
