package io.harman.flight_be.service;

import io.harman.flight_be.dto.passenger.CreatePassengerDto;
import io.harman.flight_be.dto.passenger.ReadPassengerDto;
import io.harman.flight_be.dto.passenger.UpdatePassengerDto;
import io.harman.flight_be.model.flight.Passenger;
import io.harman.flight_be.repository.flight.PassengerRepository;

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
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class PassengerServiceImplTest {

    @Mock
    private PassengerRepository passengerRepository;

    @InjectMocks
    private PassengerServiceImpl passengerService;

    private Passenger passenger1;
    private Passenger passenger2;
    private CreatePassengerDto createPassengerDto;
    private UpdatePassengerDto updatePassengerDto;
    private UUID passengerId;

    @BeforeEach
    void setUp() {
        passengerId = UUID.randomUUID();

        passenger1 = Passenger.builder()
                .id(passengerId)
                .fullName("John Doe")
                .birthDate(LocalDate.of(1990, 1, 1))
                .gender(1) // Male
                .idPassport("A1234567")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        passenger2 = Passenger.builder()
                .id(UUID.randomUUID())
                .fullName("Jane Smith")
                .birthDate(LocalDate.of(1995, 5, 15))
                .gender(2) // Female
                .idPassport("B7654321")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        createPassengerDto = CreatePassengerDto.builder()
                .fullName("New Passenger")
                .birthDate(LocalDate.of(2000, 12, 25))
                .gender(1)
                .idPassport("C9876543")
                .build();

        updatePassengerDto = UpdatePassengerDto.builder()
                .id(passengerId)
                .fullName("John Doe Updated")
                .birthDate(LocalDate.of(1990, 1, 1))
                .gender(1)
                .idPassport("A1234567")
                .build();
    }

    @Test
    void testGetAllPassengers() {
        List<Passenger> passengers = Arrays.asList(passenger1, passenger2);
        when(passengerRepository.findAllByOrderByFullNameAsc()).thenReturn(passengers);

        List<ReadPassengerDto> result = passengerService.getAllPassengers();

        assertEquals(2, result.size());
        verify(passengerRepository).findAllByOrderByFullNameAsc();
    }

    @Test
    void testGetPassengerByIdSuccess() {
        when(passengerRepository.findById(passengerId)).thenReturn(Optional.of(passenger1));

        ReadPassengerDto result = passengerService.getPassengerById(passengerId);

        assertNotNull(result);
        assertEquals(passengerId, result.getId());
        assertEquals("John Doe", result.getFullName());
        assertEquals("A1234567", result.getIdPassport());
        verify(passengerRepository).findById(passengerId);
    }

    @Test
    void testGetPassengerByIdNotFound() {
        UUID fakeId = UUID.randomUUID();
        when(passengerRepository.findById(fakeId)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> passengerService.getPassengerById(fakeId));

        assertTrue(exception.getMessage().contains("not found"));
        verify(passengerRepository).findById(fakeId);
    }

    @Test
    void testCreatePassengerSuccess() {
        when(passengerRepository.existsByIdPassport("C9876543")).thenReturn(false);
        when(passengerRepository.save(any(Passenger.class))).thenReturn(passenger1);

        ReadPassengerDto result = passengerService.createPassenger(createPassengerDto);

        assertNotNull(result);
        verify(passengerRepository).existsByIdPassport("C9876543");
        verify(passengerRepository).save(any(Passenger.class));
    }

    @Test
    void testCreatePassengerAlreadyExists() {
        when(passengerRepository.existsByIdPassport("C9876543")).thenReturn(true);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> passengerService.createPassenger(createPassengerDto));

        assertTrue(exception.getMessage().contains("already exists"));
        verify(passengerRepository).existsByIdPassport("C9876543");
        verify(passengerRepository, never()).save(any(Passenger.class));
    }

    @Test
    void testUpdatePassengerSuccess() {
        when(passengerRepository.findById(passengerId)).thenReturn(Optional.of(passenger1));
        when(passengerRepository.save(any(Passenger.class))).thenReturn(passenger1);

        ReadPassengerDto result = passengerService.updatePassenger(updatePassengerDto);

        assertNotNull(result);
        verify(passengerRepository).findById(passengerId);
        verify(passengerRepository).save(any(Passenger.class));
    }

    @Test
    void testUpdatePassengerNotFound() {
        UUID fakeId = UUID.randomUUID();
        updatePassengerDto.setId(fakeId);
        when(passengerRepository.findById(fakeId)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> passengerService.updatePassenger(updatePassengerDto));

        assertTrue(exception.getMessage().contains("not found"));
        verify(passengerRepository).findById(fakeId);
        verify(passengerRepository, never()).save(any(Passenger.class));
    }

    @Test
    void testUpdatePassengerIdPassportAlreadyExists() {
        updatePassengerDto.setIdPassport("NEWPASSPORT");
        when(passengerRepository.findById(passengerId)).thenReturn(Optional.of(passenger1));
        when(passengerRepository.existsByIdPassport("NEWPASSPORT")).thenReturn(true);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> passengerService.updatePassenger(updatePassengerDto));

        assertTrue(exception.getMessage().contains("already exists"));
        verify(passengerRepository, never()).save(any(Passenger.class));
    }

    @Test
    void testDeletePassengerSuccess() {
        when(passengerRepository.findById(passengerId)).thenReturn(Optional.of(passenger1));
        doNothing().when(passengerRepository).delete(passenger1);

        assertDoesNotThrow(() -> passengerService.deletePassenger(passengerId));

        verify(passengerRepository).findById(passengerId);
        verify(passengerRepository).delete(passenger1);
    }

    @Test
    void testDeletePassengerNotFound() {
        UUID fakeId = UUID.randomUUID();
        when(passengerRepository.findById(fakeId)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> passengerService.deletePassenger(fakeId));

        assertTrue(exception.getMessage().contains("not found"));
        verify(passengerRepository).findById(fakeId);
        verify(passengerRepository, never()).delete(any(Passenger.class));
    }

    @Test
    void testSearchPassengers() {
        List<Passenger> passengers = Arrays.asList(passenger1);
        when(passengerRepository.searchPassengers("John", null, null)).thenReturn(passengers);

        List<ReadPassengerDto> result = passengerService.searchPassengers("John", null, null);

        assertEquals(1, result.size());
        verify(passengerRepository).searchPassengers("John", null, null);
    }

    @Test
    void testGetPassengerByIdPassportSuccess() {
        when(passengerRepository.findByIdPassport("A1234567")).thenReturn(Optional.of(passenger1));

        ReadPassengerDto result = passengerService.getPassengerByIdPassport("A1234567");

        assertNotNull(result);
        assertEquals("A1234567", result.getIdPassport());
        verify(passengerRepository).findByIdPassport("A1234567");
    }

    @Test
    void testGetPassengerByIdPassportNotFound() {
        when(passengerRepository.findByIdPassport("XX")).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> passengerService.getPassengerByIdPassport("XX"));

        assertTrue(exception.getMessage().contains("not found"));
        verify(passengerRepository).findByIdPassport("XX");
    }

    @Test
    void testGetPassengersByGender() {
        List<Passenger> passengers = Arrays.asList(passenger1);
        when(passengerRepository.findByGender(1)).thenReturn(passengers);

        List<ReadPassengerDto> result = passengerService.getPassengersByGender(1);

        assertEquals(1, result.size());
        assertEquals(1, result.get(0).getGender());
        verify(passengerRepository).findByGender(1);
    }

    @Test
    void testGetAdultPassengers() {
        List<Passenger> passengers = Arrays.asList(passenger1, passenger2);
        when(passengerRepository.findAdultPassengers()).thenReturn(passengers);

        List<ReadPassengerDto> result = passengerService.getAdultPassengers();

        assertEquals(2, result.size());
        verify(passengerRepository).findAdultPassengers();
    }

    @Test
    void testGetChildPassengers() {
        Passenger child = Passenger.builder()
                .id(UUID.randomUUID())
                .fullName("Child Passenger")
                .birthDate(LocalDate.now().minusYears(10))
                .gender(1)
                .idPassport("CHILD123")
                .build();

        List<Passenger> passengers = Arrays.asList(child);
        when(passengerRepository.findChildPassengers()).thenReturn(passengers);

        List<ReadPassengerDto> result = passengerService.getChildPassengers();

        assertEquals(1, result.size());
        verify(passengerRepository).findChildPassengers();
    }

    @Test
    void testCalculateAge() {
        LocalDate birthDate = LocalDate.now().minusYears(30);

        int age = passengerService.calculateAge(birthDate);

        assertEquals(30, age);
    }

    @Test
    void testCalculateAgeNull() {
        int age = passengerService.calculateAge(null);

        assertEquals(0, age);
    }

    @Test
    void testGetGenderLabel() {
        assertEquals("Male", passengerService.getGenderLabel(1));
        assertEquals("Female", passengerService.getGenderLabel(2));
        assertEquals("Other", passengerService.getGenderLabel(3));
        assertEquals("Unknown", passengerService.getGenderLabel(99));
        assertEquals("Unknown", passengerService.getGenderLabel(null));
    }
}
