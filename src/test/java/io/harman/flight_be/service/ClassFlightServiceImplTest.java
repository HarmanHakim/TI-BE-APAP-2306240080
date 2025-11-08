package io.harman.flight_be.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import io.harman.flight_be.dto.classflight.CreateClassFlightDto;
import io.harman.flight_be.dto.classflight.ReadClassFlightDto;
import io.harman.flight_be.dto.classflight.UpdateClassFlightDto;
import io.harman.flight_be.model.ClassFlight;
import io.harman.flight_be.model.Flight;
import io.harman.flight_be.repository.ClassFlightRepository;
import io.harman.flight_be.repository.FlightRepository;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class ClassFlightServiceImplTest {

    @Mock
    private ClassFlightRepository classFlightRepository;

    @Mock
    private FlightRepository flightRepository;

    @InjectMocks
    private ClassFlightServiceImpl classFlightService;

    private ClassFlight classFlight1;
    private ClassFlight classFlight2;
    private Flight flight;
    private CreateClassFlightDto createClassFlightDto;
    private UpdateClassFlightDto updateClassFlightDto;

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
                .status(1)
                .build();

        classFlight1 = ClassFlight.builder()
                .id(1)
                .flightId("FL001")
                .classType("Economy") // Economy
                .seatCapacity(100)
                .availableSeats(100)
                .price(new BigDecimal("1000000"))
                .build();

        classFlight2 = ClassFlight.builder()
                .id(2)
                .flightId("FL001")
                .classType("Business") // Business
                .seatCapacity(40)
                .availableSeats(40)
                .price(new BigDecimal("3000000"))
                .build();

        createClassFlightDto = CreateClassFlightDto.builder()
                .flightId("FL001")
                .classType("Economy")
                .seatCapacity(100)
                .price(new BigDecimal("1000000"))
                .build();

        updateClassFlightDto = UpdateClassFlightDto.builder()
                .id(1)
                .flightId("FL001")
                .classType("Economy")
                .seatCapacity(110)
                .availableSeats(110)
                .price(new BigDecimal("1100000"))
                .build();
    }

    @Test
    void testGetAllClassFlights() {
        List<ClassFlight> classFlights = Arrays.asList(classFlight1, classFlight2);
        when(classFlightRepository.findAll()).thenReturn(classFlights);

        List<ReadClassFlightDto> result = classFlightService.getAllClassFlights();

        assertEquals(2, result.size());
        verify(classFlightRepository).findAll();
    }

    @Test
    void testGetClassFlightByIdSuccess() {
        when(classFlightRepository.findById(1)).thenReturn(Optional.of(classFlight1));

        ReadClassFlightDto result = classFlightService.getClassFlightById(1);

        assertNotNull(result);
        assertEquals(1, result.getId());
        assertEquals("Economy", result.getClassType());
        verify(classFlightRepository).findById(1);
    }

    @Test
    void testGetClassFlightByIdNotFound() {
        when(classFlightRepository.findById(999)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> classFlightService.getClassFlightById(999));

        assertTrue(exception.getMessage().contains("not found"));
        verify(classFlightRepository).findById(999);
    }

    @Test
    void testCreateClassFlightSuccess() {
        when(flightRepository.findById("FL001")).thenReturn(Optional.of(flight));
        when(classFlightRepository.existsByFlightIdAndClassType("FL001", "Economy")).thenReturn(false);
        when(classFlightRepository.save(any(ClassFlight.class))).thenReturn(classFlight1);

        ReadClassFlightDto result = classFlightService.createClassFlight(createClassFlightDto);

        assertNotNull(result);
        verify(flightRepository).findById("FL001");
        verify(classFlightRepository).existsByFlightIdAndClassType("FL001", "Economy");
        verify(classFlightRepository).save(any(ClassFlight.class));
    }

    @Test
    void testCreateClassFlightFlightNotFound() {
        when(flightRepository.findById("FL001")).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> classFlightService.createClassFlight(createClassFlightDto));

        assertTrue(exception.getMessage().contains("not found"));
        verify(flightRepository).findById("FL001");
        verify(classFlightRepository, never()).save(any(ClassFlight.class));
    }

    @Test
    void testCreateClassFlightAlreadyExists() {
        when(flightRepository.findById("FL001")).thenReturn(Optional.of(flight));
        when(classFlightRepository.existsByFlightIdAndClassType("FL001", "Economy")).thenReturn(true);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> classFlightService.createClassFlight(createClassFlightDto));

        assertTrue(
                exception.getMessage().toLowerCase().contains("already exists") ||
                        exception.getMessage().toLowerCase().contains("already"));
        verify(classFlightRepository, never()).save(any(ClassFlight.class));
    }

    @Test
    void testUpdateClassFlightSuccess() {
        when(classFlightRepository.findById(1)).thenReturn(Optional.of(classFlight1));
        when(classFlightRepository.save(any(ClassFlight.class))).thenReturn(classFlight1);

        ReadClassFlightDto result = classFlightService.updateClassFlight(updateClassFlightDto);

        assertNotNull(result);
        verify(classFlightRepository).findById(1);
        verify(classFlightRepository).save(any(ClassFlight.class));
    }

    @Test
    void testUpdateClassFlightNotFound() {
        when(classFlightRepository.findById(999)).thenReturn(Optional.empty());
        updateClassFlightDto.setId(999);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> classFlightService.updateClassFlight(updateClassFlightDto));

        assertTrue(exception.getMessage().contains("not found"));
        verify(classFlightRepository).findById(999);
        verify(classFlightRepository, never()).save(any(ClassFlight.class));
    }

    @Test
    void testDeleteClassFlightSuccess() {
        when(classFlightRepository.findById(1)).thenReturn(Optional.of(classFlight1));
        doNothing().when(classFlightRepository).delete(classFlight1);

        assertDoesNotThrow(() -> classFlightService.deleteClassFlight(1));

        verify(classFlightRepository).findById(1);
        verify(classFlightRepository).delete(classFlight1);
    }

    @Test
    void testDeleteClassFlightNotFound() {
        when(classFlightRepository.findById(999)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> classFlightService.deleteClassFlight(999));

        assertTrue(exception.getMessage().contains("not found"));
        verify(classFlightRepository).findById(999);
        verify(classFlightRepository, never()).delete(any(ClassFlight.class));
    }

    @Test
    void testGetClassFlightsByFlightId() {
        List<ClassFlight> classFlights = Arrays.asList(classFlight1, classFlight2);
        when(classFlightRepository.findByFlightId("FL001")).thenReturn(classFlights);

        List<ReadClassFlightDto> result = classFlightService.getClassFlightsByFlightId("FL001");

        assertEquals(2, result.size());
        verify(classFlightRepository).findByFlightId("FL001");
    }

    @Test
    void testGetClassFlightsWithAvailableSeats() {
        List<ClassFlight> classFlights = Arrays.asList(classFlight1, classFlight2);
        when(classFlightRepository.findAllWithAvailableSeats()).thenReturn(classFlights);

        List<ReadClassFlightDto> result = classFlightService.getClassFlightsWithAvailableSeats();

        assertEquals(2, result.size());
        verify(classFlightRepository).findAllWithAvailableSeats();
    }

    @Test
    void testGetClassFlightByFlightIdAndTypeSuccess() {
        when(classFlightRepository.findByFlightIdAndClassType("FL001", "Economy"))
                .thenReturn(Optional.of(classFlight1));

        ReadClassFlightDto result = classFlightService.getClassFlightByFlightIdAndType("FL001", "Economy");

        assertNotNull(result);
        assertEquals("Economy", result.getClassType());
        verify(classFlightRepository).findByFlightIdAndClassType("FL001", "Economy");
    }

    @Test
    void testGetClassFlightByFlightIdAndTypeNotFound() {
        when(classFlightRepository.findByFlightIdAndClassType("FL001", "Premium")).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> classFlightService.getClassFlightByFlightIdAndType("FL001", "Premium"));

        assertTrue(ex.getMessage().contains("Class Flight not found") || ex.getMessage().contains("not found"));
    }

    @Test
    void testDecreaseAvailableSeatsSuccess() {
        when(classFlightRepository.decreaseAvailableSeats(1, 2)).thenReturn(1);

        assertDoesNotThrow(() -> classFlightService.decreaseAvailableSeats(1, 2));
        verify(classFlightRepository).decreaseAvailableSeats(1, 2);
    }

    @Test
    void testDecreaseAvailableSeatsFail() {
        when(classFlightRepository.decreaseAvailableSeats(1, 5)).thenReturn(0);

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> classFlightService.decreaseAvailableSeats(1, 5));

        assertTrue(ex.getMessage().contains("Cannot decrease seats"));
    }

    @Test
    void testIncreaseAvailableSeatsSuccess() {
        when(classFlightRepository.increaseAvailableSeats(1, 3)).thenReturn(1);

        assertDoesNotThrow(() -> classFlightService.increaseAvailableSeats(1, 3));
        verify(classFlightRepository).increaseAvailableSeats(1, 3);
    }

    @Test
    void testIncreaseAvailableSeatsFail() {
        when(classFlightRepository.increaseAvailableSeats(999, 1)).thenReturn(0);

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> classFlightService.increaseAvailableSeats(999, 1));

        assertTrue(ex.getMessage().contains("Cannot increase seats"));
    }

    @Test
    void testGetTotalAvailableSeatsByFlightReturnsValue() {
        when(classFlightRepository.getTotalAvailableSeatsByFlight("FL001")).thenReturn(140);

        Integer total = classFlightService.getTotalAvailableSeatsByFlight("FL001");
        assertEquals(140, total);
    }

    @Test
    void testGetTotalAvailableSeatsByFlightNullReturnsZero() {
        when(classFlightRepository.getTotalAvailableSeatsByFlight("FL001")).thenReturn(null);

        Integer total = classFlightService.getTotalAvailableSeatsByFlight("FL001");
        assertEquals(0, total);
    }
}
