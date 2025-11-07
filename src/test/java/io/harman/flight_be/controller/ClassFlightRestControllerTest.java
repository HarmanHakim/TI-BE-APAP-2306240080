package io.harman.flight_be.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.harman.flight_be.dto.classflight.CreateClassFlightDto;
import io.harman.flight_be.dto.classflight.ReadClassFlightDto;
import io.harman.flight_be.dto.classflight.UpdateClassFlightDto;
import io.harman.flight_be.service.ClassFlightService;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ClassFlightRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ClassFlightService classFlightService;

    private ReadClassFlightDto classFlightDto1;
    private ReadClassFlightDto classFlightDto2;
    private CreateClassFlightDto createClassFlightDto;
    private UpdateClassFlightDto updateClassFlightDto;

    @BeforeEach
    void setUp() {
        classFlightDto1 = ReadClassFlightDto.builder()
                .id(1)
                .flightId("FL001")
                .classType("Economy")
                .seatCapacity(100)
                .availableSeats(100)
                .price(new BigDecimal("1000000"))
                .build();

        classFlightDto2 = ReadClassFlightDto.builder()
                .id(2)
                .flightId("FL001")
                .classType("Business")
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
    void testGetAllClassFlights() throws Exception {
        List<ReadClassFlightDto> classFlights = Arrays.asList(classFlightDto1, classFlightDto2);
        when(classFlightService.getAllClassFlights()).thenReturn(classFlights);

        mockMvc.perform(get("/api/class-flights/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data", hasSize(2)));

        verify(classFlightService).getAllClassFlights();
    }

    @Test
    void testGetClassFlightByIdSuccess() throws Exception {
        when(classFlightService.getClassFlightById(1)).thenReturn(classFlightDto1);

        mockMvc.perform(get("/api/class-flights/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data.id").value(1));

        verify(classFlightService).getClassFlightById(1);
    }

    @Test
    void testGetClassFlightByIdNotFound() throws Exception {
        when(classFlightService.getClassFlightById(999)).thenThrow(new RuntimeException("Class Flight not found"));

        mockMvc.perform(get("/api/class-flights/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));

        verify(classFlightService).getClassFlightById(999);
    }

    @Test
    void testCreateClassFlightSuccess() throws Exception {
        when(classFlightService.createClassFlight(any(CreateClassFlightDto.class))).thenReturn(classFlightDto1);

        mockMvc.perform(post("/api/class-flights/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createClassFlightDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value(201))
                .andExpect(jsonPath("$.data").exists());

        verify(classFlightService).createClassFlight(any(CreateClassFlightDto.class));
    }

    @Test
    void testUpdateClassFlightSuccess() throws Exception {
        when(classFlightService.updateClassFlight(any(UpdateClassFlightDto.class))).thenReturn(classFlightDto1);

        mockMvc.perform(put("/api/class-flights/1/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateClassFlightDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data").exists());

        verify(classFlightService).updateClassFlight(any(UpdateClassFlightDto.class));
    }

    @Test
    void testDeleteClassFlightSuccess() throws Exception {
        doNothing().when(classFlightService).deleteClassFlight(1);

        mockMvc.perform(delete("/api/class-flights/1/delete"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200));

        verify(classFlightService).deleteClassFlight(1);
    }
}
