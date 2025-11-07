package io.harman.flight_be.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.harman.flight_be.dto.seat.CreateSeatDto;
import io.harman.flight_be.dto.seat.ReadSeatDto;
import io.harman.flight_be.dto.seat.UpdateSeatDto;
import io.harman.flight_be.service.SeatService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class SeatRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private SeatService seatService;

    private ReadSeatDto seatDto1;
    private ReadSeatDto seatDto2;
    private CreateSeatDto createSeatDto;
    private UpdateSeatDto updateSeatDto;

    @BeforeEach
    void setUp() {
        seatDto1 = ReadSeatDto.builder()
                .id(1L)
                .classFlightId(1)
                .seatNumber("1A")
                .isAvailable(true)
                .passengerId(null)
                .build();

        seatDto2 = ReadSeatDto.builder()
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
    void testGetAllSeats() throws Exception {
        List<ReadSeatDto> seats = Arrays.asList(seatDto1, seatDto2);
        when(seatService.getAllSeats()).thenReturn(seats);

        mockMvc.perform(get("/api/seats/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data", hasSize(2)));

        verify(seatService).getAllSeats();
    }

    @Test
    void testGetSeatByIdSuccess() throws Exception {
        when(seatService.getSeatById(1L)).thenReturn(seatDto1);

        mockMvc.perform(get("/api/seats/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data.id").value(1));

        verify(seatService).getSeatById(1L);
    }

    @Test
    void testGetSeatByIdNotFound() throws Exception {
        when(seatService.getSeatById(999L)).thenThrow(new RuntimeException("Seat not found"));

        mockMvc.perform(get("/api/seats/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));

        verify(seatService).getSeatById(999L);
    }

    @Test
    void testCreateSeatSuccess() throws Exception {
        when(seatService.createSeat(any(CreateSeatDto.class))).thenReturn(seatDto1);

        mockMvc.perform(post("/api/seats/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createSeatDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value(201))
                .andExpect(jsonPath("$.data").exists());

        verify(seatService).createSeat(any(CreateSeatDto.class));
    }

    @Test
    void testUpdateSeatSuccess() throws Exception {
        when(seatService.updateSeat(any(UpdateSeatDto.class))).thenReturn(seatDto1);

        mockMvc.perform(put("/api/seats/1/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateSeatDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data").exists());

        verify(seatService).updateSeat(any(UpdateSeatDto.class));
    }

    @Test
    void testDeleteSeatSuccess() throws Exception {
        doNothing().when(seatService).deleteSeat(1L);

        mockMvc.perform(delete("/api/seats/1/delete"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200));

        verify(seatService).deleteSeat(1L);
    }
}
