package io.harman.flight_be.service;

import io.harman.flight_be.dto.flight.CreateFlightDto;
import io.harman.flight_be.dto.flight.ReadFlightDto;
import io.harman.flight_be.dto.flight.UpdateFlightDto;
import io.harman.flight_be.model.flight.Airline;
import io.harman.flight_be.model.flight.Airplane;
import io.harman.flight_be.model.flight.ClassFlight;
import io.harman.flight_be.model.flight.Flight;
import io.harman.flight_be.repository.flight.AirlineRepository;
import io.harman.flight_be.repository.flight.AirplaneRepository;
import io.harman.flight_be.repository.flight.BookingRepository;
import io.harman.flight_be.repository.flight.FlightRepository;

import java.math.BigDecimal;

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

    @Test
    void testGetFlightsDepartingToday() {
        List<Flight> flights = Arrays.asList(flight1);
        when(flightRepository.findFlightsDepartingOnDate(any(LocalDateTime.class))).thenReturn(flights);

        List<ReadFlightDto> result = flightService.getFlightsDepartingToday();

        assertEquals(1, result.size());
        verify(flightRepository).findFlightsDepartingOnDate(any(LocalDateTime.class));
    }

    @Test
    void testGetFlightsByAirline() {
        List<Flight> flights = Arrays.asList(flight1);
        when(flightRepository.findByAirlineIdAndIsDeletedFalse("GA")).thenReturn(flights);

        List<ReadFlightDto> result = flightService.getFlightsByAirline("GA");

        assertEquals(1, result.size());
        verify(flightRepository).findByAirlineIdAndIsDeletedFalse("GA");
    }

    @Test
    void testGetFlightsWithAvailableSeats() {
        // Prepare a class flight and add to flight1
        ClassFlight classFlight = ClassFlight.builder()
                .id(1)
                .classType("Economy")
                .seatCapacity(180)
                .availableSeats(50)
                .price(new BigDecimal("150000"))
                .build();

        flight1.setClasses(Arrays.asList(classFlight));
        flight1.setStatus(2); // In Flight to test status label mapping

        List<Flight> flights = Arrays.asList(flight1);
        when(flightRepository.findFlightsWithAvailableSeats()).thenReturn(flights);

        List<ReadFlightDto> result = flightService.getFlightsWithAvailableSeats();

        assertEquals(1, result.size());

        ReadFlightDto dto = result.get(0);
        // classesSummary mapping
        assertNotNull(dto.getClasses());
        assertEquals(1, dto.getClasses().size());
        ReadFlightDto.ClassFlightSummary summary = dto.getClasses().get(0);
        assertEquals(1, summary.getId());
        assertEquals("Economy", summary.getClassType());
        assertEquals(180, summary.getSeatCapacity());
        assertEquals(50, summary.getAvailableSeats());
        assertEquals(new BigDecimal("150000"), summary.getPrice());

        // status label mapping for status 2
        assertEquals("In Flight", dto.getStatusLabel());

        verify(flightRepository).findFlightsWithAvailableSeats();
    }

    @Test
    void testIsAirplaneAvailableNoExistingFlights() {
        when(flightRepository.findByAirplaneIdAndIsDeletedFalse("AP001")).thenReturn(Arrays.asList());

        boolean available = flightService.isAirplaneAvailable("AP001",
                LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(1).plusHours(2), null);

        assertTrue(available);
        verify(flightRepository).findByAirplaneIdAndIsDeletedFalse("AP001");
    }

    @Test
    void testIsAirplaneAvailableOverlappingReturnsFalse() {
        LocalDateTime existingDep = LocalDateTime.now().plusDays(1);
        LocalDateTime existingArr = existingDep.plusHours(2);

        Flight existing = Flight.builder().id("FL100").airplaneId("AP001").departureTime(existingDep)
                .arrivalTime(existingArr).status(1).isDeleted(false).build();

        when(flightRepository.findByAirplaneIdAndIsDeletedFalse("AP001")).thenReturn(Arrays.asList(existing));

        // new flight overlaps existing
        LocalDateTime newDep = existingDep.plusMinutes(30);
        LocalDateTime newArr = existingDep.plusHours(1);

        boolean available = flightService.isAirplaneAvailable("AP001", newDep, newArr, null);

        assertEquals(false, available);
    }

    @Test
    void testIsAirplaneAvailableNonOverlappingReturnsTrue() {
        LocalDateTime existingDep = LocalDateTime.now().plusDays(1);
        LocalDateTime existingArr = existingDep.plusHours(2);

        Flight existing = Flight.builder().id("FL101").airplaneId("AP001").departureTime(existingDep)
                .arrivalTime(existingArr).status(1).isDeleted(false).build();

        when(flightRepository.findByAirplaneIdAndIsDeletedFalse("AP001")).thenReturn(Arrays.asList(existing));

        // new flight starts after existing arrival
        LocalDateTime newDep = existingArr.plusHours(1);
        LocalDateTime newArr = newDep.plusHours(2);

        boolean available = flightService.isAirplaneAvailable("AP001", newDep, newArr, null);

        assertTrue(available);
    }

    @Test
    void testIsAirplaneAvailableSkipsExcludeFlightId() {
        LocalDateTime existingDep = LocalDateTime.now().plusDays(1);
        LocalDateTime existingArr = existingDep.plusHours(2);

        Flight existing = Flight.builder().id("FL200").airplaneId("AP001").departureTime(existingDep)
                .arrivalTime(existingArr).status(1).isDeleted(false).build();

        when(flightRepository.findByAirplaneIdAndIsDeletedFalse("AP001")).thenReturn(Arrays.asList(existing));

        // overlapping but exclude the same flight id
        LocalDateTime newDep = existingDep.plusMinutes(15);
        LocalDateTime newArr = existingDep.plusHours(1);

        boolean available = flightService.isAirplaneAvailable("AP001", newDep, newArr, "FL200");

        // should be true because the only conflicting flight is excluded
        assertTrue(available);
    }

    @Test
    void testIsAirplaneAvailableIgnoresNonCheckedStatuses() {
        LocalDateTime existingDep = LocalDateTime.now().plusDays(1);
        LocalDateTime existingArr = existingDep.plusHours(2);

        // status 3 (Finished) should be ignored
        Flight existing = Flight.builder().id("FL300").airplaneId("AP001").departureTime(existingDep)
                .arrivalTime(existingArr).status(3).isDeleted(false).build();

        when(flightRepository.findByAirplaneIdAndIsDeletedFalse("AP001")).thenReturn(Arrays.asList(existing));

        LocalDateTime newDep = existingDep.plusMinutes(15);
        LocalDateTime newArr = existingDep.plusHours(1);

        boolean available = flightService.isAirplaneAvailable("AP001", newDep, newArr, null);

        // should be true because existing flight status is not 1,2,4
        assertTrue(available);
    }

    @Test
    void testStatusLabelScheduled() {
        Flight f = Flight.builder().id("S1").status(1).isDeleted(false).build();
        when(flightRepository.findAll()).thenReturn(Arrays.asList(f));

        List<ReadFlightDto> dtos = flightService.getAllFlights();
        assertEquals(1, dtos.size());
        assertEquals("Scheduled", dtos.get(0).getStatusLabel());
    }

    @Test
    void testStatusLabelInFlight() {
        Flight f = Flight.builder().id("S2").status(2).isDeleted(false).build();
        when(flightRepository.findAll()).thenReturn(Arrays.asList(f));

        List<ReadFlightDto> dtos = flightService.getAllFlights();
        assertEquals(1, dtos.size());
        assertEquals("In Flight", dtos.get(0).getStatusLabel());
    }

    @Test
    void testStatusLabelFinished() {
        Flight f = Flight.builder().id("S3").status(3).isDeleted(false).build();
        when(flightRepository.findAll()).thenReturn(Arrays.asList(f));

        List<ReadFlightDto> dtos = flightService.getAllFlights();
        assertEquals(1, dtos.size());
        assertEquals("Finished", dtos.get(0).getStatusLabel());
    }

    @Test
    void testStatusLabelDelayed() {
        Flight f = Flight.builder().id("S4").status(4).isDeleted(false).build();
        when(flightRepository.findAll()).thenReturn(Arrays.asList(f));

        List<ReadFlightDto> dtos = flightService.getAllFlights();
        assertEquals(1, dtos.size());
        assertEquals("Delayed", dtos.get(0).getStatusLabel());
    }

    @Test
    void testStatusLabelCancelled() {
        Flight f = Flight.builder().id("S5").status(5).isDeleted(false).build();
        when(flightRepository.findAll()).thenReturn(Arrays.asList(f));

        List<ReadFlightDto> dtos = flightService.getAllFlights();
        assertEquals(1, dtos.size());
        assertEquals("Cancelled", dtos.get(0).getStatusLabel());
    }

    @Test
    void testStatusLabelUnknownNull() {
        Flight f = Flight.builder().id("S6").status(null).isDeleted(false).build();
        when(flightRepository.findAll()).thenReturn(Arrays.asList(f));

        List<ReadFlightDto> dtos = flightService.getAllFlights();
        assertEquals(1, dtos.size());
        assertEquals("Unknown", dtos.get(0).getStatusLabel());
    }

    @Test
    void testStatusLabelUnknownDefault() {
        Flight f = Flight.builder().id("S7").status(999).isDeleted(false).build();
        when(flightRepository.findAll()).thenReturn(Arrays.asList(f));

        List<ReadFlightDto> dtos = flightService.getAllFlights();
        assertEquals(1, dtos.size());
        assertEquals("Unknown", dtos.get(0).getStatusLabel());
    }
}
