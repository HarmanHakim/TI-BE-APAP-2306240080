package io.harman.flight_be.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.harman.flight_be.dto.airplane.CreateAirplaneDto;
import io.harman.flight_be.dto.airplane.ReadAirplaneDto;
import io.harman.flight_be.dto.airplane.UpdateAirplaneDto;
import io.harman.flight_be.service.AirplaneService;
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
class AirplaneRestControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @Autowired
        private ObjectMapper objectMapper;

        @MockBean
        private AirplaneService airplaneService;

        private ReadAirplaneDto airplaneDto1;
        private ReadAirplaneDto airplaneDto2;
        private CreateAirplaneDto createAirplaneDto;
        private UpdateAirplaneDto updateAirplaneDto;

        @BeforeEach
        void setUp() {
                airplaneDto1 = ReadAirplaneDto.builder()
                                .id("AP001")
                                .airlineId("GA")
                                .model("Boeing 737")
                                .seatCapacity(180)
                                .manufactureYear(2020)
                                .createdAt(LocalDateTime.now())
                                .build();

                airplaneDto2 = ReadAirplaneDto.builder()
                                .id("AP002")
                                .airlineId("GA")
                                .model("Airbus A320")
                                .seatCapacity(160)
                                .manufactureYear(2021)
                                .createdAt(LocalDateTime.now())
                                .build();

                createAirplaneDto = CreateAirplaneDto.builder()
                                .airlineId("GA")
                                .model("Boeing 777")
                                .seatCapacity(300)
                                .manufactureYear(2022)
                                .build();

                updateAirplaneDto = UpdateAirplaneDto.builder()
                                .airlineId("GA")
                                .model("Boeing 737 MAX")
                                .seatCapacity(190)
                                .manufactureYear(2020)
                                .build();
        }

        @Test
        void testGetAllAirplanes() throws Exception {
                List<ReadAirplaneDto> airplanes = Arrays.asList(airplaneDto1, airplaneDto2);
                when(airplaneService.getAllAirplanes()).thenReturn(airplanes);

                mockMvc.perform(get("/api/airplanes/all"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.status").value(200))
                                .andExpect(jsonPath("$.data", hasSize(2)));

                verify(airplaneService).getAllAirplanes();
        }

        @Test
        void testGetAirplaneByIdSuccess() throws Exception {
                when(airplaneService.getAirplaneById("AP001")).thenReturn(airplaneDto1);

                mockMvc.perform(get("/api/airplanes/AP001"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.status").value(200))
                                .andExpect(jsonPath("$.data.id").value("AP001"));

                verify(airplaneService).getAirplaneById("AP001");
        }

        @Test
        void testGetAirplaneByIdNotFound() throws Exception {
                when(airplaneService.getAirplaneById("XX")).thenThrow(new RuntimeException("Airplane not found"));

                mockMvc.perform(get("/api/airplanes/XX"))
                                .andExpect(status().isNotFound())
                                .andExpect(jsonPath("$.status").value(404));

                verify(airplaneService).getAirplaneById("XX");
        }

        @Test
        void testCreateAirplaneSuccess() throws Exception {
                when(airplaneService.createAirplane(any(CreateAirplaneDto.class))).thenReturn(airplaneDto1);

                mockMvc.perform(post("/api/airplanes/create")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(createAirplaneDto)))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.status").value(201))
                                .andExpect(jsonPath("$.data").exists());

                verify(airplaneService).createAirplane(any(CreateAirplaneDto.class));
        }

        @Test
        void testUpdateAirplaneSuccess() throws Exception {
                when(airplaneService.updateAirplane(any(UpdateAirplaneDto.class))).thenReturn(airplaneDto1);

                mockMvc.perform(put("/api/airplanes/AP001/update")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(updateAirplaneDto)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.status").value(200))
                                .andExpect(jsonPath("$.data").exists());

                verify(airplaneService).updateAirplane(any(UpdateAirplaneDto.class));
        }

        @Test
        void testDeleteAirplaneSuccess() throws Exception {
                doNothing().when(airplaneService).deleteAirplane("AP001");

                mockMvc.perform(delete("/api/airplanes/AP001/delete"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.status").value(200));

                verify(airplaneService).deleteAirplane("AP001");
        }
}
