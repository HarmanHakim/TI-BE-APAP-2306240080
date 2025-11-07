package io.harman.flight_be.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.harman.flight_be.dto.flight.CreateFlightDto;
import io.harman.flight_be.dto.flight.ReadFlightDto;
import io.harman.flight_be.dto.flight.UpdateFlightDto;
import io.harman.flight_be.service.FlightService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class FlightRestControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @Autowired
        private ObjectMapper objectMapper;

        @MockBean
        private FlightService flightService;

        private ReadFlightDto flightDto1;
        private ReadFlightDto flightDto2;
        private CreateFlightDto createFlightDto;
        private UpdateFlightDto updateFlightDto;

        @BeforeEach
        void setUp() {
                LocalDateTime departureTime = LocalDateTime.now().plusDays(1);
                LocalDateTime arrivalTime = departureTime.plusHours(2);

                flightDto1 = ReadFlightDto.builder()
                                .id("FL001")
                                .airlineId("GA")
                                .airplaneId("AP001")
                                .originAirportCode("CGK")
                                .destinationAirportCode("DPS")
                                .departureTime(departureTime)
                                .arrivalTime(arrivalTime)
                                .terminal("Terminal 1")
                                .gate("A1")
                                .status(1)
                                .build();

                flightDto2 = ReadFlightDto.builder()
                                .id("FL002")
                                .airlineId("GA")
                                .airplaneId("AP002")
                                .originAirportCode("DPS")
                                .destinationAirportCode("CGK")
                                .departureTime(departureTime.plusDays(1))
                                .arrivalTime(arrivalTime.plusDays(1))
                                .terminal("Terminal 2")
                                .gate("B2")
                                .status(1)
                                .build();

                createFlightDto = CreateFlightDto.builder()
                                .airlineId("GA")
                                .airplaneId("AP001")
                                .originAirportCode("CGK")
                                .destinationAirportCode("DPS")
                                .departureTime(departureTime)
                                .arrivalTime(arrivalTime)
                                .terminal("Terminal 1")
                                .gate("A1")
                                .baggageAllowance(20)
                                .facilities("WiFi")
                                .status(1)
                                .build();

                updateFlightDto = UpdateFlightDto.builder()
                                .airlineId("GA")
                                .airplaneId("AP001")
                                .originAirportCode("CGK")
                                .destinationAirportCode("DPS")
                                .departureTime(departureTime)
                                .arrivalTime(arrivalTime)
                                .terminal("Terminal 1")
                                .gate("A2")
                                .baggageAllowance(25)
                                .facilities("WiFi, Meal")
                                .status(1)
                                .build();
        }

        @Test
        void testGetAllFlights() throws Exception {
                List<ReadFlightDto> flights = Arrays.asList(flightDto1, flightDto2);
                when(flightService.getAllFlights()).thenReturn(flights);

                mockMvc.perform(get("/api/flights/all"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.status").value(200))
                                .andExpect(jsonPath("$.message").value("Flights retrieved successfully"))
                                .andExpect(jsonPath("$.data", hasSize(2)));

                verify(flightService).getAllFlights();
        }

        @Test
        void testGetAllFlightsWithFilters() throws Exception {
                List<ReadFlightDto> flights = Arrays.asList(flightDto1);
                when(flightService.searchFlights(eq("CGK"), eq("DPS"), any(), any(), any())).thenReturn(flights);

                mockMvc.perform(get("/api/flights/all")
                                .param("origin", "CGK")
                                .param("destination", "DPS"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.status").value(200))
                                .andExpect(jsonPath("$.data", hasSize(1)));

                verify(flightService).searchFlights(eq("CGK"), eq("DPS"), any(), any(), any());
        }

        @Test
        void testGetFlightByIdSuccess() throws Exception {
                when(flightService.getFlightById("FL001")).thenReturn(flightDto1);

                mockMvc.perform(get("/api/flights/FL001"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.status").value(200))
                                .andExpect(jsonPath("$.message").value("Flight retrieved successfully"))
                                .andExpect(jsonPath("$.data.id").value("FL001"));

                verify(flightService).getFlightById("FL001");
        }

        @Test
        void testGetFlightByIdNotFound() throws Exception {
                when(flightService.getFlightById("XX")).thenThrow(new RuntimeException("Flight with ID XX not found"));

                mockMvc.perform(get("/api/flights/XX"))
                                .andExpect(status().isNotFound())
                                .andExpect(jsonPath("$.status").value(404))
                                .andExpect(jsonPath("$.message").value(containsString("not found")));

                verify(flightService).getFlightById("XX");
        }

        @Test
        void testCreateFlightSuccess() throws Exception {
                when(flightService.createFlight(any(CreateFlightDto.class))).thenReturn(flightDto1);

                mockMvc.perform(post("/api/flights/create")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(createFlightDto)))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.status").value(201))
                                .andExpect(jsonPath("$.message").value("Flight created successfully"))
                                .andExpect(jsonPath("$.data").exists());

                verify(flightService).createFlight(any(CreateFlightDto.class));
        }

        @Test
        void testCreateFlightValidationError() throws Exception {
                CreateFlightDto invalidDto = CreateFlightDto.builder()
                                .airlineId("")
                                .airplaneId("")
                                .originAirportCode("")
                                .destinationAirportCode("")
                                .build();

                mockMvc.perform(post("/api/flights/create")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(invalidDto)))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.status").value(400));
        }

        @Test
        void testUpdateFlightSuccess() throws Exception {
                when(flightService.updateFlight(any(UpdateFlightDto.class))).thenReturn(flightDto1);

                mockMvc.perform(put("/api/flights/FL001/update")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(updateFlightDto)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.status").value(200))
                                .andExpect(jsonPath("$.message").value("Flight updated successfully"))
                                .andExpect(jsonPath("$.data").exists());

                verify(flightService).updateFlight(any(UpdateFlightDto.class));
        }

        @Test
        void testUpdateFlightNotFound() throws Exception {
                when(flightService.updateFlight(any(UpdateFlightDto.class)))
                                .thenThrow(new RuntimeException("Flight with ID XX not found"));

                mockMvc.perform(put("/api/flights/XX/update")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(updateFlightDto)))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.status").value(400))
                                .andExpect(jsonPath("$.message").value(containsString("not found")));
        }

        @Test
        void testDeleteFlightSuccess() throws Exception {
                doNothing().when(flightService).deleteFlight("FL001");

                mockMvc.perform(delete("/api/flights/FL001/delete"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.status").value(200))
                                .andExpect(jsonPath("$.message").value(containsString("cancelled successfully")));

                verify(flightService).deleteFlight("FL001");
        }

        @Test
        void testDeleteFlightNotFound() throws Exception {
                doThrow(new RuntimeException("Flight with ID XX not found"))
                                .when(flightService).deleteFlight("XX");

                mockMvc.perform(delete("/api/flights/XX/delete"))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.status").value(400))
                                .andExpect(jsonPath("$.message").value(containsString("not found")));
        }

        @Test
        void testGetAllFlightsException() throws Exception {
                when(flightService.getAllFlights()).thenThrow(new RuntimeException("Database error"));

                mockMvc.perform(get("/api/flights/all"))
                                .andExpect(status().isInternalServerError())
                                .andExpect(jsonPath("$.status").value(500))
                                .andExpect(jsonPath("$.message").value(containsString("Failed to retrieve flights")));
        }
}
