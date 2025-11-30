package io.harman.flight_be.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.harman.flight_be.dto.airplane.CreateAirplaneDto;
import io.harman.flight_be.dto.airplane.ReadAirplaneDto;
import io.harman.flight_be.dto.airplane.UpdateAirplaneDto;
import io.harman.flight_be.service.AirplaneService;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@WithMockUser(authorities = {"SUPERADMIN"})
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
                                .airlineId("GAI")
                                .model("Boeing 737")
                                .seatCapacity(180)
                                .manufactureYear(2020)
                                .createdAt(LocalDateTime.now())
                                .build();

                airplaneDto2 = ReadAirplaneDto.builder()
                                .id("AP002")
                                .airlineId("GAI")
                                .model("Airbus A320")
                                .seatCapacity(160)
                                .manufactureYear(2021)
                                .createdAt(LocalDateTime.now())
                                .build();

                createAirplaneDto = CreateAirplaneDto.builder()
                                .airlineId("GAI")
                                .model("Boeing 777")
                                .seatCapacity(300)
                                .manufactureYear(2022)
                                .build();

                updateAirplaneDto = UpdateAirplaneDto.builder()
                                .airlineId("GAI")
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

        @Test
        void testActivateAirplaneSuccess() throws Exception {
                doNothing().when(airplaneService).activateAirplane("AP001");

                mockMvc.perform(post("/api/airplanes/AP001/activate"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.status").value(200));

                verify(airplaneService).activateAirplane("AP001");
        }

        @Test
        void testActivateAirplaneBadRequest() throws Exception {
                doThrow(new RuntimeException("Already active")).when(airplaneService).activateAirplane("AP002");

                mockMvc.perform(post("/api/airplanes/AP002/activate"))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.status").value(400));

                verify(airplaneService).activateAirplane("AP002");
        }

        @Test
        void testGetAllAirplanesServerError() throws Exception {
                when(airplaneService.getAllAirplanes()).thenThrow(new RuntimeException("DB down"));

                mockMvc.perform(get("/api/airplanes/all"))
                                .andExpect(status().isInternalServerError())
                                .andExpect(jsonPath("$.status").value(500));

                verify(airplaneService).getAllAirplanes();
        }

        @Test
        void testGetAirplaneByIdServerError() throws Exception {
                when(airplaneService.getAirplaneById("APX")).thenThrow(new RuntimeException("Unexpected"));

                // Service throws RuntimeException -> controller maps to NOT_FOUND for this
                // endpoint
                mockMvc.perform(get("/api/airplanes/APX"))
                                .andExpect(status().isNotFound())
                                .andExpect(jsonPath("$.status").value(404));

                verify(airplaneService).getAirplaneById("APX");
        }

        @Test
        void testCreateAirplaneServerError() throws Exception {
                when(airplaneService.createAirplane(any(CreateAirplaneDto.class)))
                                .thenThrow(new RuntimeException("create failed"));

                mockMvc.perform(post("/api/airplanes/create")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(createAirplaneDto)))
                                // Service throws RuntimeException -> controller maps to BAD_REQUEST for create
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.status").value(400));

                verify(airplaneService).createAirplane(any(CreateAirplaneDto.class));
        }

        @Test
        void testUpdateAirplaneServerError() throws Exception {
                when(airplaneService.updateAirplane(any(UpdateAirplaneDto.class)))
                                .thenThrow(new RuntimeException("update failed"));

                mockMvc.perform(put("/api/airplanes/AP001/update")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(updateAirplaneDto)))
                                // Service throws RuntimeException -> controller maps to BAD_REQUEST for update
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.status").value(400));

                verify(airplaneService).updateAirplane(any(UpdateAirplaneDto.class));
        }

        @Test
        void testDeleteAirplaneServerError() throws Exception {
                doThrow(new RuntimeException("delete failed")).when(airplaneService).deleteAirplane("AP001");

                // Service throws RuntimeException -> controller maps to BAD_REQUEST for delete
                mockMvc.perform(delete("/api/airplanes/AP001/delete"))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.status").value(400));

                verify(airplaneService).deleteAirplane("AP001");
        }

        @Test
        void testActivateAirplaneServerError() throws Exception {
                doThrow(new RuntimeException("activate failed")).when(airplaneService).activateAirplane("AP001");

                // Service throws RuntimeException -> controller maps to BAD_REQUEST for
                // activate
                mockMvc.perform(post("/api/airplanes/AP001/activate"))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.status").value(400));

                verify(airplaneService).activateAirplane("AP001");
        }

        @Test
        void testGetAllAirplanesCheckedExceptionServerError() throws Exception {
                when(airplaneService.getAllAirplanes()).thenAnswer(inv -> {
                        throw new Exception("checked DB down");
                });

                mockMvc.perform(get("/api/airplanes/all"))
                                .andExpect(status().isInternalServerError())
                                .andExpect(jsonPath("$.status").value(500));

                verify(airplaneService).getAllAirplanes();
        }

        @Test
        void testGetAirplaneByIdCheckedExceptionServerError() throws Exception {
                when(airplaneService.getAirplaneById("APX")).thenAnswer(inv -> {
                        throw new Exception("checked unexpected");
                });

                mockMvc.perform(get("/api/airplanes/APX"))
                                .andExpect(status().isInternalServerError())
                                .andExpect(jsonPath("$.status").value(500));

                verify(airplaneService).getAirplaneById("APX");
        }

        @Test
        void testCreateAirplaneCheckedExceptionServerError() throws Exception {
                when(airplaneService.createAirplane(any(CreateAirplaneDto.class)))
                                .thenAnswer(inv -> {
                                        throw new Exception("checked create failed");
                                });

                mockMvc.perform(post("/api/airplanes/create")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(createAirplaneDto)))
                                .andExpect(status().isInternalServerError())
                                .andExpect(jsonPath("$.status").value(500));

                verify(airplaneService).createAirplane(any(CreateAirplaneDto.class));
        }

        @Test
        void testUpdateAirplaneCheckedExceptionServerError() throws Exception {
                when(airplaneService.updateAirplane(any(UpdateAirplaneDto.class)))
                                .thenAnswer(inv -> {
                                        throw new Exception("checked update failed");
                                });

                mockMvc.perform(put("/api/airplanes/AP001/update")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(updateAirplaneDto)))
                                .andExpect(status().isInternalServerError())
                                .andExpect(jsonPath("$.status").value(500));

                verify(airplaneService).updateAirplane(any(UpdateAirplaneDto.class));
        }

        @Test
        void testDeleteAirplaneCheckedExceptionServerError() throws Exception {
                // use doAnswer for void method to throw checked Exception
                org.mockito.Mockito.doAnswer(inv -> {
                        throw new Exception("checked delete failed");
                })
                                .when(airplaneService).deleteAirplane("AP001");

                mockMvc.perform(delete("/api/airplanes/AP001/delete"))
                                .andExpect(status().isInternalServerError())
                                .andExpect(jsonPath("$.status").value(500));

                verify(airplaneService).deleteAirplane("AP001");
        }

        @Test
        void testActivateAirplaneCheckedExceptionServerError() throws Exception {
                org.mockito.Mockito.doAnswer(inv -> {
                        throw new Exception("checked activate failed");
                })
                                .when(airplaneService).activateAirplane("AP001");

                mockMvc.perform(post("/api/airplanes/AP001/activate"))
                                .andExpect(status().isInternalServerError())
                                .andExpect(jsonPath("$.status").value(500));

                verify(airplaneService).activateAirplane("AP001");
        }
}
