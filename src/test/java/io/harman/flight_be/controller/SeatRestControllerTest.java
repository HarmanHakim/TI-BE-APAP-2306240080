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
import org.springframework.security.test.context.support.WithMockUser;
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
@WithMockUser(authorities = {"SUPERADMIN"})
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

        @Test
        void testDeleteSeatNotFound() throws Exception {
                doThrow(new RuntimeException("Seat not found")).when(seatService).deleteSeat(999L);

                mockMvc.perform(delete("/api/seats/999/delete"))
                                .andExpect(status().isNotFound())
                                .andExpect(jsonPath("$.status").value(404))
                                .andExpect(jsonPath("$.message", containsString("Seat not found")));

                verify(seatService).deleteSeat(999L);
        }

        @Test
        void testAssignSeatSuccess() throws Exception {
                UUID pid = UUID.randomUUID();
                when(seatService.assignSeatToPassenger(eq(1L), eq(pid), eq(1))).thenReturn(seatDto1);

                mockMvc.perform(post("/api/seats/1/assign")
                                .param("passengerId", pid.toString())
                                .param("classFlightId", "1"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.status").value(200))
                                .andExpect(jsonPath("$.data.id").value(1));

                verify(seatService).assignSeatToPassenger(eq(1L), eq(pid), eq(1));
        }

        @Test
        void testAssignSeatBadRequest() throws Exception {
                UUID pid = UUID.randomUUID();
                when(seatService.assignSeatToPassenger(eq(1L), eq(pid), eq(1)))
                                .thenThrow(new RuntimeException("Seat does not belong to the specified class flight"));

                mockMvc.perform(post("/api/seats/1/assign")
                                .param("passengerId", pid.toString())
                                .param("classFlightId", "1"))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.status").value(400))
                                .andExpect(jsonPath("$.message", containsString("does not belong")));
        }

        @Test
        void testReleaseSeatSuccess() throws Exception {
                when(seatService.releaseSeat(1L)).thenReturn(seatDto1);

                mockMvc.perform(post("/api/seats/1/release"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.status").value(200))
                                .andExpect(jsonPath("$.data.id").value(1));

                verify(seatService).releaseSeat(1L);
        }

        @Test
        void testReleaseSeatBadRequest() throws Exception {
                when(seatService.releaseSeat(1L)).thenThrow(new RuntimeException("Cannot release seat"));

                mockMvc.perform(post("/api/seats/1/release"))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.status").value(400))
                                .andExpect(jsonPath("$.message", containsString("Cannot release")));
        }

        @Test
        void testGetAvailableSeatsEndpointSuccess() throws Exception {
                List<ReadSeatDto> seats = Arrays.asList(seatDto1, seatDto2);
                when(seatService.getSeatsByClassFlightId(1)).thenReturn(seats);

                mockMvc.perform(get("/api/seats/available").param("classFlightId", "1"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.status").value(200))
                                .andExpect(jsonPath("$.data", hasSize(1)))
                                .andExpect(jsonPath("$.data[0].seatNumber").value("1A"));

                verify(seatService).getSeatsByClassFlightId(1);
        }

        @Test
        void testGetAvailableSeatsEndpointServerError() throws Exception {
                when(seatService.getSeatsByClassFlightId(1)).thenThrow(new RuntimeException("DB down"));

                mockMvc.perform(get("/api/seats/available").param("classFlightId", "1"))
                                .andExpect(status().isInternalServerError())
                                .andExpect(jsonPath("$.status").value(500))
                                .andExpect(jsonPath("$.message", containsString("Failed to retrieve available seats")));
        }

        @Test
        void testGetAllSeatsCheckedExceptionServerError() throws Exception {
                when(seatService.getAllSeats()).thenAnswer(inv -> {
                        throw new Exception("checked DB");
                });

                mockMvc.perform(get("/api/seats/all"))
                                .andExpect(status().isInternalServerError())
                                .andExpect(jsonPath("$.status").value(500));

                verify(seatService).getAllSeats();
        }

        @Test
        void testGetSeatByIdCheckedExceptionServerError() throws Exception {
                when(seatService.getSeatById(1L)).thenAnswer(inv -> {
                        throw new Exception("checked unexpected");
                });

                mockMvc.perform(get("/api/seats/1"))
                                .andExpect(status().isInternalServerError())
                                .andExpect(jsonPath("$.status").value(500));

                verify(seatService).getSeatById(1L);
        }

        @Test
        void testCreateSeatCheckedExceptionServerError() throws Exception {
                when(seatService.createSeat(any(CreateSeatDto.class))).thenAnswer(inv -> {
                        throw new Exception("checked create failed");
                });

                mockMvc.perform(post("/api/seats/create")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(createSeatDto)))
                                .andExpect(status().isInternalServerError())
                                .andExpect(jsonPath("$.status").value(500));

                verify(seatService).createSeat(any(CreateSeatDto.class));
        }

        @Test
        void testUpdateSeatCheckedExceptionServerError() throws Exception {
                when(seatService.updateSeat(any(UpdateSeatDto.class))).thenAnswer(inv -> {
                        throw new Exception("checked update failed");
                });

                mockMvc.perform(put("/api/seats/1/update")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(updateSeatDto)))
                                .andExpect(status().isInternalServerError())
                                .andExpect(jsonPath("$.status").value(500));

                verify(seatService).updateSeat(any(UpdateSeatDto.class));
        }

        @Test
        void testDeleteSeatCheckedExceptionServerError() throws Exception {
                org.mockito.Mockito.doAnswer(inv -> {
                        throw new Exception("checked delete failed");
                }).when(seatService).deleteSeat(1L);

                mockMvc.perform(delete("/api/seats/1/delete"))
                                .andExpect(status().isInternalServerError())
                                .andExpect(jsonPath("$.status").value(500));

                verify(seatService).deleteSeat(1L);
        }

        @Test
        void testAssignSeatCheckedExceptionServerError() throws Exception {
                java.util.UUID pid = java.util.UUID.randomUUID();
                when(seatService.assignSeatToPassenger(eq(1L), eq(pid), eq(1))).thenAnswer(inv -> {
                        throw new Exception("checked assign failed");
                });

                mockMvc.perform(post("/api/seats/1/assign").param("passengerId", pid.toString()).param("classFlightId",
                                "1"))
                                .andExpect(status().isInternalServerError())
                                .andExpect(jsonPath("$.status").value(500));

                verify(seatService).assignSeatToPassenger(eq(1L), eq(pid), eq(1));
        }

        @Test
        void testReleaseSeatCheckedExceptionServerError() throws Exception {
                when(seatService.releaseSeat(1L)).thenAnswer(inv -> {
                        throw new Exception("checked release failed");
                });

                mockMvc.perform(post("/api/seats/1/release"))
                                .andExpect(status().isInternalServerError())
                                .andExpect(jsonPath("$.status").value(500));

                verify(seatService).releaseSeat(1L);
        }

        @Test
        void testGetAvailableSeatsEndpointCheckedExceptionServerError() throws Exception {
                when(seatService.getSeatsByClassFlightId(1)).thenAnswer(inv -> {
                        throw new Exception("checked DB down");
                });

                mockMvc.perform(get("/api/seats/available").param("classFlightId", "1"))
                                .andExpect(status().isInternalServerError())
                                .andExpect(jsonPath("$.status").value(500));

                verify(seatService).getSeatsByClassFlightId(1);
        }
}
