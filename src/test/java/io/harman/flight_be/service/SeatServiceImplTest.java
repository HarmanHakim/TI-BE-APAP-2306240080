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
import io.harman.flight_be.model.ClassFlight;
import io.harman.flight_be.model.Seat;
import io.harman.flight_be.repository.ClassFlightRepository;
import io.harman.flight_be.repository.PassengerRepository;
import io.harman.flight_be.repository.SeatRepository;

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
}
