package io.harman.flight_be.service;

import io.harman.flight_be.dto.airline.CreateAirlineDto;
import io.harman.flight_be.dto.airline.ReadAirlineDto;
import io.harman.flight_be.dto.airline.UpdateAirlineDto;
import io.harman.flight_be.model.flight.Airline;
import io.harman.flight_be.repository.flight.AirlineRepository;

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
class AirlineServiceImplTest {

    @Mock
    private AirlineRepository airlineRepository;

    @InjectMocks
    private AirlineServiceImpl airlineService;

    private Airline airline1;
    private Airline airline2;
    private CreateAirlineDto createAirlineDto;
    private UpdateAirlineDto updateAirlineDto;

    @BeforeEach
    void setUp() {
        airline1 = Airline.builder()
                .id("GA")
                .name("Garuda Indonesia")
                .country("Indonesia")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        airline2 = Airline.builder()
                .id("QZ")
                .name("AirAsia")
                .country("Malaysia")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        createAirlineDto = CreateAirlineDto.builder()
                .id("SQ")
                .name("Singapore Airlines")
                .country("Singapore")
                .build();

        updateAirlineDto = UpdateAirlineDto.builder()
                .id("GA")
                .name("Garuda Indonesia Updated")
                .country("Indonesia")
                .build();
    }

    @Test
    void testGetAllAirlines() {
        List<Airline> airlines = Arrays.asList(airline1, airline2);
        when(airlineRepository.findAllByOrderByNameAsc()).thenReturn(airlines);

        List<ReadAirlineDto> result = airlineService.getAllAirlines();

        assertEquals(2, result.size());
        assertEquals("Garuda Indonesia", result.get(0).getName());
        assertEquals("AirAsia", result.get(1).getName());
        verify(airlineRepository).findAllByOrderByNameAsc();
    }

    @Test
    void testGetAirlineByIdSuccess() {
        when(airlineRepository.findById("GA")).thenReturn(Optional.of(airline1));

        ReadAirlineDto result = airlineService.getAirlineById("GA");

        assertNotNull(result);
        assertEquals("GA", result.getId());
        assertEquals("Garuda Indonesia", result.getName());
        assertEquals("Indonesia", result.getCountry());
        verify(airlineRepository).findById("GA");
    }

    @Test
    void testGetAirlineByIdNotFound() {
        when(airlineRepository.findById("XX")).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> airlineService.getAirlineById("XX"));

        assertTrue(exception.getMessage().contains("not found"));
        verify(airlineRepository).findById("XX");
    }

    @Test
    void testCreateAirlineSuccess() {
        when(airlineRepository.existsById("SQ")).thenReturn(false);
        when(airlineRepository.save(any(Airline.class))).thenReturn(airline1);

        ReadAirlineDto result = airlineService.createAirline(createAirlineDto);

        assertNotNull(result);
        verify(airlineRepository).existsById("SQ");
        verify(airlineRepository).save(any(Airline.class));
    }

    @Test
    void testCreateAirlineAlreadyExists() {
        when(airlineRepository.existsById("SQ")).thenReturn(true);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> airlineService.createAirline(createAirlineDto));

        assertTrue(exception.getMessage().contains("already exists"));
        verify(airlineRepository).existsById("SQ");
        verify(airlineRepository, never()).save(any(Airline.class));
    }

    @Test
    void testUpdateAirlineSuccess() {
        when(airlineRepository.findById("GA")).thenReturn(Optional.of(airline1));
        when(airlineRepository.save(any(Airline.class))).thenReturn(airline1);

        ReadAirlineDto result = airlineService.updateAirline(updateAirlineDto);

        assertNotNull(result);
        verify(airlineRepository).findById("GA");
        verify(airlineRepository).save(any(Airline.class));
    }

    @Test
    void testUpdateAirlineNotFound() {
        when(airlineRepository.findById("XX")).thenReturn(Optional.empty());
        updateAirlineDto.setId("XX");

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> airlineService.updateAirline(updateAirlineDto));

        assertTrue(exception.getMessage().contains("not found"));
        verify(airlineRepository).findById("XX");
        verify(airlineRepository, never()).save(any(Airline.class));
    }

    @Test
    void testDeleteAirlineSuccess() {
        when(airlineRepository.findById("GA")).thenReturn(Optional.of(airline1));
        doNothing().when(airlineRepository).delete(airline1);

        assertDoesNotThrow(() -> airlineService.deleteAirline("GA"));

        verify(airlineRepository).findById("GA");
        verify(airlineRepository).delete(airline1);
    }

    @Test
    void testDeleteAirlineNotFound() {
        when(airlineRepository.findById("XX")).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> airlineService.deleteAirline("XX"));

        assertTrue(exception.getMessage().contains("not found"));
        verify(airlineRepository).findById("XX");
        verify(airlineRepository, never()).delete(any(Airline.class));
    }

    @Test
    void testGetAirlinesByCountry() {
        List<Airline> airlines = Arrays.asList(airline1);
        when(airlineRepository.findByCountryOrderByNameAsc("Indonesia")).thenReturn(airlines);

        List<ReadAirlineDto> result = airlineService.getAirlinesByCountry("Indonesia");

        assertEquals(1, result.size());
        assertEquals("Indonesia", result.get(0).getCountry());
        verify(airlineRepository).findByCountryOrderByNameAsc("Indonesia");
    }

    @Test
    void testSearchAirlinesByName() {
        List<Airline> airlines = Arrays.asList(airline1);
        when(airlineRepository.findByNameContainingIgnoreCase("Garuda")).thenReturn(airlines);

        List<ReadAirlineDto> result = airlineService.searchAirlinesByName("Garuda");

        assertEquals(1, result.size());
        assertTrue(result.get(0).getName().contains("Garuda"));
        verify(airlineRepository).findByNameContainingIgnoreCase("Garuda");
    }

    @Test
    void testGetDistinctCountries() {
        List<String> countries = Arrays.asList("Indonesia", "Malaysia", "Singapore");
        when(airlineRepository.findDistinctCountries()).thenReturn(countries);

        List<String> result = airlineService.getDistinctCountries();

        assertEquals(3, result.size());
        assertTrue(result.contains("Indonesia"));
        assertTrue(result.contains("Malaysia"));
        verify(airlineRepository).findDistinctCountries();
    }

    @Test
    void testCountAirlinesByCountry() {
        when(airlineRepository.countByCountry("Indonesia")).thenReturn(5L);

        long result = airlineService.countAirlinesByCountry("Indonesia");

        assertEquals(5L, result);
        verify(airlineRepository).countByCountry("Indonesia");
    }

    @Test
    void testExistsById() {
        when(airlineRepository.existsById("GA")).thenReturn(true);

        boolean result = airlineService.existsById("GA");

        assertTrue(result);
        verify(airlineRepository).existsById("GA");
    }
}
