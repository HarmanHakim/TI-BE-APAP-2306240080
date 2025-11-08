package io.harman.flight_be.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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

import io.harman.flight_be.dto.airplane.CreateAirplaneDto;
import io.harman.flight_be.dto.airplane.ReadAirplaneDto;
import io.harman.flight_be.dto.airplane.UpdateAirplaneDto;
import io.harman.flight_be.model.Airline;
import io.harman.flight_be.model.Airplane;
import io.harman.flight_be.model.Flight;
import io.harman.flight_be.repository.AirlineRepository;
import io.harman.flight_be.repository.AirplaneRepository;
import io.harman.flight_be.repository.FlightRepository;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class AirplaneServiceImplTest {

    @Mock
    private AirplaneRepository airplaneRepository;

    @Mock
    private AirlineRepository airlineRepository;

    @Mock
    private FlightRepository flightRepository;

    @InjectMocks
    private AirplaneServiceImpl airplaneService;

    private Airplane airplane1;
    private Airplane airplane2;
    private Airline airline;
    private CreateAirplaneDto createAirplaneDto;
    private UpdateAirplaneDto updateAirplaneDto;

    @BeforeEach
    void setUp() {
        airline = Airline.builder()
                .id("GA")
                .name("Garuda Indonesia")
                .country("Indonesia")
                .build();

        airplane1 = Airplane.builder()
                .id("AP001")
                .airlineId("GA")
                .model("Boeing 737")
                .seatCapacity(180)
                .manufactureYear(2020)
                .isDeleted(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        airplane2 = Airplane.builder()
                .id("AP002")
                .airlineId("GA")
                .model("Airbus A320")
                .seatCapacity(160)
                .manufactureYear(2021)
                .isDeleted(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        createAirplaneDto = CreateAirplaneDto.builder()
                .airlineId("GA")
                .model("Boeing 777")
                .seatCapacity(300)
                .manufactureYear(2022)
                .build();

        updateAirplaneDto = UpdateAirplaneDto.builder()
                .id("AP001")
                .airlineId("GA")
                .model("Boeing 737 MAX")
                .seatCapacity(190)
                .manufactureYear(2020)
                .build();
    }

    @Test
    void testGetAllAirplanes() {
        List<Airplane> airplanes = Arrays.asList(airplane1, airplane2);
        when(airplaneRepository.findAll()).thenReturn(airplanes);

        List<ReadAirplaneDto> result = airplaneService.getAllAirplanes();

        assertEquals(2, result.size());
        verify(airplaneRepository).findAll();
    }

    @Test
    void testGetAllActiveAirplanes() {
        List<Airplane> airplanes = Arrays.asList(airplane1, airplane2);
        when(airplaneRepository.findByIsDeletedFalse()).thenReturn(airplanes);

        List<ReadAirplaneDto> result = airplaneService.getAllActiveAirplanes();

        assertEquals(2, result.size());
        verify(airplaneRepository).findByIsDeletedFalse();
    }

    @Test
    void testGetAirplaneByIdSuccess() {
        when(airplaneRepository.findById("AP001")).thenReturn(Optional.of(airplane1));

        ReadAirplaneDto result = airplaneService.getAirplaneById("AP001");

        assertNotNull(result);
        assertEquals("AP001", result.getId());
        assertEquals("Boeing 737", result.getModel());
        verify(airplaneRepository).findById("AP001");
    }

    @Test
    void testGetAirplaneByIdNotFound() {
        when(airplaneRepository.findById("XX")).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> airplaneService.getAirplaneById("XX"));

        assertTrue(exception.getMessage().contains("not found"));
        verify(airplaneRepository).findById("XX");
    }

    @Test
    void testCreateAirplaneSuccess() {
        when(airlineRepository.findById("GA")).thenReturn(Optional.of(airline));
        when(airplaneRepository.save(any(Airplane.class))).thenReturn(airplane1);

        ReadAirplaneDto result = airplaneService.createAirplane(createAirplaneDto);

        assertNotNull(result);
        verify(airlineRepository).findById("GA");
        verify(airplaneRepository).save(any(Airplane.class));
    }

    @Test
    void testCreateAirplaneAirlineNotFound() {
        when(airlineRepository.findById("GA")).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> airplaneService.createAirplane(createAirplaneDto));

        assertTrue(exception.getMessage().contains("not found"));
        verify(airlineRepository).findById("GA");
        verify(airplaneRepository, never()).save(any(Airplane.class));
    }

    @Test
    void testUpdateAirplaneSuccess() {
        when(airplaneRepository.findById("AP001")).thenReturn(Optional.of(airplane1));
        when(airplaneRepository.save(any(Airplane.class))).thenReturn(airplane1);

        ReadAirplaneDto result = airplaneService.updateAirplane(updateAirplaneDto);

        assertNotNull(result);
        verify(airplaneRepository).findById("AP001");
        verify(airplaneRepository).save(any(Airplane.class));
    }

    @Test
    void testUpdateAirplaneNotFound() {
        when(airplaneRepository.findById("XX")).thenReturn(Optional.empty());
        updateAirplaneDto.setId("XX");

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> airplaneService.updateAirplane(updateAirplaneDto));

        assertTrue(exception.getMessage().contains("not found"));
        verify(airplaneRepository).findById("XX");
        verify(airplaneRepository, never()).save(any(Airplane.class));
    }

    @Test
    void testUpdateAirplaneInactive() {
        airplane1.setIsDeleted(true);
        when(airplaneRepository.findById("AP001")).thenReturn(Optional.of(airplane1));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> airplaneService.updateAirplane(updateAirplaneDto));

        assertTrue(exception.getMessage().contains("inactive"));
        verify(airplaneRepository, never()).save(any(Airplane.class));
    }

    @Test
    void testDeleteAirplaneSuccess() {
        when(airplaneRepository.findById("AP001")).thenReturn(Optional.of(airplane1));
        when(airplaneRepository.save(any(Airplane.class))).thenReturn(airplane1);

        assertDoesNotThrow(() -> airplaneService.deleteAirplane("AP001"));

        verify(airplaneRepository).findById("AP001");
        verify(airplaneRepository).save(any(Airplane.class));
    }

    @Test
    void testDeleteAirplaneNotFound() {
        when(airplaneRepository.findById("XX")).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> airplaneService.deleteAirplane("XX"));

        assertTrue(exception.getMessage().contains("not found"));
        verify(airplaneRepository).findById("XX");
        verify(airplaneRepository, never()).save(any(Airplane.class));
    }

    @Test
    void testDeleteAirplaneCancelsRelatedFlights() {
        when(airplaneRepository.findById("AP001")).thenReturn(Optional.of(airplane1));
        when(airplaneRepository.save(any(Airplane.class))).thenReturn(airplane1);

        Flight related = Flight.builder()
                .id("FL100")
                .status(3) // Finished so can delete
                .isDeleted(false)
                .build();

        when(flightRepository.findByAirplaneIdAndIsDeletedFalse("AP001")).thenReturn(Arrays.asList(related));
        when(flightRepository.save(any(Flight.class))).thenReturn(related);

        assertDoesNotThrow(() -> airplaneService.deleteAirplane("AP001"));

        // airplane should be marked deleted and saved
        verify(airplaneRepository).save(any(Airplane.class));
        // related flight should be cancelled and saved
        verify(flightRepository).save(any(Flight.class));
    }

    @Test
    void testDeleteAirplaneCannotDeleteWhenActiveFlightsExist() {
        when(airplaneRepository.findById("AP001")).thenReturn(Optional.of(airplane1));

        Flight active = Flight.builder()
                .id("FL101")
                .status(1) // Scheduled -> prevents deletion
                .isDeleted(false)
                .build();

        when(flightRepository.findByAirplaneIdAndIsDeletedFalse("AP001")).thenReturn(Arrays.asList(active));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> airplaneService.deleteAirplane("AP001"));
        assertTrue(ex.getMessage().contains("Cannot delete airplane"));

        verify(airplaneRepository, never()).save(any(Airplane.class));
    }

    @Test
    void testActivateAirplane() {
        airplane1.setIsDeleted(true);
        when(airplaneRepository.findById("AP001")).thenReturn(Optional.of(airplane1));
        when(airplaneRepository.save(any(Airplane.class))).thenReturn(airplane1);

        airplaneService.activateAirplane("AP001");

        assertFalse(airplane1.getIsDeleted());
        verify(airplaneRepository).save(any(Airplane.class));
    }

    @Test
    void testGetAirplanesByAirlineId() {
        List<Airplane> list = Arrays.asList(airplane1, airplane2);
        when(airplaneRepository.findByAirlineIdAndIsDeletedFalse("GA")).thenReturn(list);

        List<ReadAirplaneDto> result = airplaneService.getAirplanesByAirlineId("GA");

        assertEquals(2, result.size());
        verify(airplaneRepository).findByAirlineIdAndIsDeletedFalse("GA");
    }

    @Test
    void testSearchAirplanesFilters() {
        Airplane airplane3 = Airplane.builder()
                .id("AP003")
                .airlineId("XX")
                .model("Embraer")
                .seatCapacity(50)
                .manufactureYear(2019)
                .isDeleted(false)
                .build();

        List<Airplane> all = Arrays.asList(airplane1, airplane2, airplane3);
        when(airplaneRepository.findAll()).thenReturn(all);

        // filter by model (case-insensitive) - ensure AP001 appears in results
        List<ReadAirplaneDto> r1 = airplaneService.searchAirplanes(null, "boeing", null, null);
        assertTrue(r1.stream().anyMatch(dto -> "AP001".equals(dto.getId())));

        // filter by airlineId
        List<ReadAirplaneDto> r2 = airplaneService.searchAirplanes("GA", null, null, null);
        assertEquals(2, r2.size());

        // filter by manufactureYear - ensure AP002 appears in results
        List<ReadAirplaneDto> r3 = airplaneService.searchAirplanes(null, null, 2021, null);
        assertTrue(r3.stream().anyMatch(dto -> "AP002".equals(dto.getId())));

        // filter by isDeleted
        airplane2.setIsDeleted(true);
        List<ReadAirplaneDto> r4 = airplaneService.searchAirplanes(null, null, null, false);
        // airplane2 is deleted so it should not appear; ensure AP001 present and AP002
        // absent
        assertTrue(r4.stream().anyMatch(dto -> "AP001".equals(dto.getId())));
        assertTrue(r4.stream().noneMatch(dto -> "AP002".equals(dto.getId())));
    }

    @Test
    void testGetAirplanesByManufactureYearRange() {
        List<Airplane> list = Arrays.asList(airplane1, airplane2);
        when(airplaneRepository.findByManufactureYearBetween(2019, 2021)).thenReturn(list);

        List<ReadAirplaneDto> result = airplaneService.getAirplanesByManufactureYearRange(2019, 2021);

        assertEquals(2, result.size());
        verify(airplaneRepository).findByManufactureYearBetween(2019, 2021);
    }
}
