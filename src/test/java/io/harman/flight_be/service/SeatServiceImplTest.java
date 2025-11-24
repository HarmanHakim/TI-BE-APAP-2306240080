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

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import io.harman.flight_be.dto.seat.CreateSeatDto;
import io.harman.flight_be.dto.seat.ReadSeatDto;
import io.harman.flight_be.dto.seat.UpdateSeatDto;
import io.harman.flight_be.model.flight.ClassFlight;
import io.harman.flight_be.model.flight.Flight;
import io.harman.flight_be.model.flight.Passenger;
import io.harman.flight_be.model.flight.Seat;
import io.harman.flight_be.repository.flight.ClassFlightRepository;
import io.harman.flight_be.repository.flight.PassengerRepository;
import io.harman.flight_be.repository.flight.SeatRepository;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class SeatServiceImplTest {

    @Mock
    private SeatRepository seatRepository;

    @Mock
    private ClassFlightRepository classFlightRepository;

    @Mock
    private PassengerRepository passengerRepository;

    @InjectMocks
    private SeatServiceImpl seatService;

    private Seat seat1;
    private Seat seat2;
    private ClassFlight classFlight;
    private CreateSeatDto createSeatDto;
    private UpdateSeatDto updateSeatDto;

    @BeforeEach
    void setUp() {
        classFlight = ClassFlight.builder()
                .id(1)
                .flightId("FL001")
                .classType("Economy")
                .build();

        seat1 = Seat.builder()
                .id(1L)
                .classFlightId(1)
                .seatNumber("1A")
                .isAvailable(true)
                .passengerId(null)
                .build();

        seat2 = Seat.builder()
                .id(2L)
                .classFlightId(1)
                .seatNumber("1B")
                .isAvailable(false)
                .passengerId(UUID.randomUUID())
                .build();

        createSeatDto = CreateSeatDto.builder()
                .classFlightId(1)
                .seatNumber("1C")
                .passengerId(null)
                .build();

        updateSeatDto = UpdateSeatDto.builder()
                .id(1L)
                .classFlightId(1)
                .seatNumber("1A")
                .isAvailable(false)
                .passengerId(UUID.randomUUID())
                .build();
    }

    @Test
    void testGetAllSeats() {
        List<Seat> seats = Arrays.asList(seat1, seat2);
        when(seatRepository.findAll()).thenReturn(seats);

        List<ReadSeatDto> result = seatService.getAllSeats();

        assertEquals(2, result.size());
        verify(seatRepository).findAll();
    }

    @Test
    void testGetSeatByIdSuccess() {
        when(seatRepository.findById(1L)).thenReturn(Optional.of(seat1));

        ReadSeatDto result = seatService.getSeatById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("1A", result.getSeatNumber());
        verify(seatRepository).findById(1L);
    }

    @Test
    void testGetSeatByIdNotFound() {
        when(seatRepository.findById(999L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> seatService.getSeatById(999L));

        assertTrue(exception.getMessage().contains("not found"));
        verify(seatRepository).findById(999L);
    }

    @Test
    void testCreateSeatSuccess() {
        when(classFlightRepository.findById(1)).thenReturn(Optional.of(classFlight));
        when(seatRepository.existsByClassFlightIdAndSeatNumber(1, "1C")).thenReturn(false);
        when(seatRepository.save(any(Seat.class))).thenReturn(seat1);

        ReadSeatDto result = seatService.createSeat(createSeatDto);

        assertNotNull(result);
        verify(classFlightRepository).findById(1);
        verify(seatRepository).existsByClassFlightIdAndSeatNumber(1, "1C");
        verify(seatRepository).save(any(Seat.class));
    }

    @Test
    void testCreateSeatClassFlightNotFound() {
        when(classFlightRepository.findById(1)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> seatService.createSeat(createSeatDto));

        assertTrue(exception.getMessage().contains("not found"));
        verify(classFlightRepository).findById(1);
        verify(seatRepository, never()).save(any(Seat.class));
    }

    @Test
    void testCreateSeatAlreadyExists() {
        when(classFlightRepository.findById(1)).thenReturn(Optional.of(classFlight));
        when(seatRepository.existsByClassFlightIdAndSeatNumber(1, "1C")).thenReturn(true);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> seatService.createSeat(createSeatDto));

        assertTrue(exception.getMessage().contains("already exists"));
        verify(seatRepository, never()).save(any(Seat.class));
    }

    @Test
    void testUpdateSeatSuccess() {
        when(seatRepository.findById(1L)).thenReturn(Optional.of(seat1));
        when(seatRepository.save(any(Seat.class))).thenReturn(seat1);

        ReadSeatDto result = seatService.updateSeat(updateSeatDto);

        assertNotNull(result);
        verify(seatRepository).findById(1L);
        verify(seatRepository).save(any(Seat.class));
    }

    @Test
    void testUpdateSeatNotFound() {
        when(seatRepository.findById(999L)).thenReturn(Optional.empty());
        updateSeatDto.setId(999L);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> seatService.updateSeat(updateSeatDto));

        assertTrue(exception.getMessage().contains("not found"));
        verify(seatRepository).findById(999L);
        verify(seatRepository, never()).save(any(Seat.class));
    }

    @Test
    void testDeleteSeatSuccess() {
        when(seatRepository.findById(1L)).thenReturn(Optional.of(seat1));
        doNothing().when(seatRepository).delete(seat1);

        assertDoesNotThrow(() -> seatService.deleteSeat(1L));

        verify(seatRepository).findById(1L);
        verify(seatRepository).delete(seat1);
    }

    @Test
    void testDeleteSeatNotFound() {
        when(seatRepository.findById(999L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> seatService.deleteSeat(999L));

        assertTrue(exception.getMessage().contains("not found"));
        verify(seatRepository).findById(999L);
        verify(seatRepository, never()).delete(any(Seat.class));
    }

    @Test
    void testGetSeatsByClassFlightId() {
        List<Seat> seats = Arrays.asList(seat1, seat2);
        when(seatRepository.findByClassFlightIdOrderBySeatNumberAsc(1)).thenReturn(seats);

        List<ReadSeatDto> result = seatService.getSeatsByClassFlightId(1);

        assertEquals(2, result.size());
        verify(seatRepository).findByClassFlightIdOrderBySeatNumberAsc(1);
    }

    @Test
    void testGetAvailableSeatsByClassFlightId() {
        List<Seat> seats = Arrays.asList(seat1);
        when(seatRepository.findByClassFlightIdAndIsAvailableTrueOrderBySeatNumberAsc(1)).thenReturn(seats);

        List<ReadSeatDto> result = seatService.getAvailableSeatsByClassFlightId(1);

        assertEquals(1, result.size());
        assertTrue(result.get(0).getIsAvailable());
        verify(seatRepository).findByClassFlightIdAndIsAvailableTrueOrderBySeatNumberAsc(1);
    }

    @Test
    void testGetSeatsByFlightId() {
        // attach classFlight with flight to seat1 to test mapping
        Flight flight = Flight.builder().id("FL001").build();
        classFlight.setFlight(flight);
        seat1.setClassFlight(classFlight);

        List<Seat> seats = Arrays.asList(seat1, seat2);
        when(seatRepository.findByFlightId("FL001")).thenReturn(seats);

        List<ReadSeatDto> result = seatService.getSeatsByFlightId("FL001");

        assertEquals(2, result.size());
        // first seat should map flightId and classType
        ReadSeatDto dto = result.get(0);
        assertEquals("FL001", dto.getFlightId());
        assertEquals("Economy", dto.getClassType());
        verify(seatRepository).findByFlightId("FL001");
    }

    @Test
    void testGetAvailableSeatsByFlightId() {
        List<Seat> seats = Arrays.asList(seat1);
        when(seatRepository.findAvailableSeatsByFlightId("FL001")).thenReturn(seats);

        List<ReadSeatDto> result = seatService.getAvailableSeatsByFlightId("FL001");

        assertEquals(1, result.size());
        assertTrue(result.get(0).getIsAvailable());
        verify(seatRepository).findAvailableSeatsByFlightId("FL001");
    }

    @Test
    void testAssignSeatToPassengerSuccess() {
        UUID passengerId = UUID.randomUUID();
        Passenger passenger = Passenger.builder().id(passengerId).fullName("John Doe").build();

        when(passengerRepository.findById(passengerId)).thenReturn(Optional.of(passenger));
        when(seatRepository.findById(1L)).thenReturn(Optional.of(seat1), Optional.of(Seat.builder()
                .id(1L)
                .classFlightId(1)
                .seatNumber("1A")
                .isAvailable(false)
                .passengerId(passengerId)
                .build()));
        when(seatRepository.assignSeatToPassenger(1L, passengerId)).thenReturn(1);

        ReadSeatDto result = seatService.assignSeatToPassenger(1L, passengerId);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(passengerId, result.getPassengerId());
        verify(passengerRepository).findById(passengerId);
        verify(seatRepository).assignSeatToPassenger(1L, passengerId);
    }

    @Test
    void testAssignSeatToPassengerSeatNotFound() {
        UUID passengerId = UUID.randomUUID();
        Passenger passenger = Passenger.builder().id(passengerId).fullName("John Doe").build();

        when(passengerRepository.findById(passengerId)).thenReturn(Optional.of(passenger));
        when(seatRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> seatService.assignSeatToPassenger(1L, passengerId));

        assertTrue(ex.getMessage().contains("not found"));
    }

    @Test
    void testAssignSeatToPassengerAlreadyAssigned() {
        UUID passengerId = UUID.randomUUID();
        Passenger passenger = Passenger.builder().id(passengerId).fullName("John Doe").build();

        seat2.setIsAvailable(false);
        when(passengerRepository.findById(passengerId)).thenReturn(Optional.of(passenger));
        when(seatRepository.findById(2L)).thenReturn(Optional.of(seat2));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> seatService.assignSeatToPassenger(2L, passengerId));

        assertTrue(ex.getMessage().contains("already assigned"));
    }

    @Test
    void testAssignSeatToPassengerWithClassFlightSuccess() {
        UUID passengerId = UUID.randomUUID();
        Passenger passenger = Passenger.builder().id(passengerId).fullName("Jane Doe").build();

        when(passengerRepository.findById(passengerId)).thenReturn(Optional.of(passenger));
        when(seatRepository.findById(1L)).thenReturn(Optional.of(seat1), Optional.of(Seat.builder()
                .id(1L)
                .classFlightId(1)
                .seatNumber("1A")
                .isAvailable(false)
                .passengerId(passengerId)
                .build()));
        when(seatRepository.assignSeatToPassenger(1L, passengerId)).thenReturn(1);

        ReadSeatDto result = seatService.assignSeatToPassenger(1L, passengerId, 1);

        assertNotNull(result);
        assertEquals(passengerId, result.getPassengerId());
    }

    @Test
    void testAssignSeatToPassengerWithClassFlightMismatch() {
        UUID passengerId = UUID.randomUUID();
        Passenger passenger = Passenger.builder().id(passengerId).fullName("Jane Doe").build();

        seat1.setClassFlightId(1);
        when(passengerRepository.findById(passengerId)).thenReturn(Optional.of(passenger));
        when(seatRepository.findById(1L)).thenReturn(Optional.of(seat1));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> seatService.assignSeatToPassenger(1L, passengerId, 2));

        assertTrue(ex.getMessage().contains("does not belong"));
    }

    @Test
    void testReleaseSeatSuccess() {
        when(seatRepository.releaseSeat(1L)).thenReturn(1);
        when(seatRepository.findById(1L)).thenReturn(Optional.of(seat1));

        ReadSeatDto result = seatService.releaseSeat(1L);

        assertNotNull(result);
        verify(seatRepository).releaseSeat(1L);
    }

    @Test
    void testReleaseSeatNotFound() {
        when(seatRepository.releaseSeat(999L)).thenReturn(0);

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> seatService.releaseSeat(999L));

        assertTrue(ex.getMessage().contains("not found"));
    }

    @Test
    void testReleaseSeatsByPassenger() {
        UUID pid = UUID.randomUUID();
        // no stubbing required for void/non-void repository method - just verify
        // interaction
        seatService.releaseSeatsByPassenger(pid);

        verify(seatRepository).releaseSeatsByPassenger(pid);
    }

    @Test
    void testIsSeatAvailable() {
        when(seatRepository.isSeatAvailable(1L)).thenReturn(true);
        when(seatRepository.isSeatAvailable(2L)).thenReturn(false);

        assertTrue(seatService.isSeatAvailable(1L));
        assertEquals(false, seatService.isSeatAvailable(2L));
    }

    @Test
    void testGenerateSeatCode() {
        String code = seatService.generateSeatCode("FL001", "Economy", 12L);
        assertEquals("FL001-EC012", code);

        // classType null or short -> prefix not applied
        assertEquals("FL001-005", seatService.generateSeatCode("FL001", "X", 5L));
    }
}
