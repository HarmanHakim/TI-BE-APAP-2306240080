package io.harman.flight_be.controller;

import static org.hamcrest.Matchers.containsString;
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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.harman.flight_be.dto.airline.CreateAirlineDto;
import io.harman.flight_be.dto.airline.ReadAirlineDto;
import io.harman.flight_be.dto.airline.UpdateAirlineDto;
import io.harman.flight_be.service.AirlineService;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@WithMockUser(authorities = { "SUPERADMIN" })
class AirlineRestControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @Autowired
        private ObjectMapper objectMapper;

        @MockBean
        private AirlineService airlineService;

        private ReadAirlineDto airlineDto1;
        private ReadAirlineDto airlineDto2;
        private CreateAirlineDto createAirlineDto;
        private UpdateAirlineDto updateAirlineDto;

        @BeforeEach
        void setUp() {
                airlineDto1 = ReadAirlineDto.builder()
                                .id("GA")
                                .name("Garuda Indonesia")
                                .country("Indonesia")
                                .createdAt(LocalDateTime.now())
                                .updatedAt(LocalDateTime.now())
                                .build();

                airlineDto2 = ReadAirlineDto.builder()
                                .id("QZ")
                                .name("AirAsia")
                                .country("Malaysia")
                                .createdAt(LocalDateTime.now())
                                .updatedAt(LocalDateTime.now())
                                .build();

                createAirlineDto = CreateAirlineDto.builder()
                                .id("SIA")
                                .name("Singapore Airlines")
                                .country("Singapore")
                                .build();

                updateAirlineDto = UpdateAirlineDto.builder()
                                .name("Garuda Indonesia Updated")
                                .country("Indonesia")
                                .build();
        }

        @Test
        void testGetAllAirlines() throws Exception {
                List<ReadAirlineDto> airlines = Arrays.asList(airlineDto1, airlineDto2);
                when(airlineService.getAllAirlines()).thenReturn(airlines);

                mockMvc.perform(get("/api/airlines/all"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.status").value(200))
                                .andExpect(jsonPath("$.message").value("Airlines retrieved successfully"))
                                .andExpect(jsonPath("$.data", hasSize(2)))
                                .andExpect(jsonPath("$.data[0].id").value("GA"))
                                .andExpect(jsonPath("$.data[1].id").value("QZ"));

                verify(airlineService).getAllAirlines();
        }

        @Test
        void testGetAllAirlinesByCountry() throws Exception {
                List<ReadAirlineDto> airlines = Arrays.asList(airlineDto1);
                when(airlineService.getAirlinesByCountry("Indonesia")).thenReturn(airlines);

                mockMvc.perform(get("/api/airlines/all")
                                .param("country", "Indonesia"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.status").value(200))
                                .andExpect(jsonPath("$.data", hasSize(1)))
                                .andExpect(jsonPath("$.data[0].country").value("Indonesia"));

                verify(airlineService).getAirlinesByCountry("Indonesia");
        }

        @Test
        void testGetAllAirlinesException() throws Exception {
                when(airlineService.getAllAirlines()).thenThrow(new RuntimeException("Database error"));

                mockMvc.perform(get("/api/airlines/all"))
                                .andExpect(status().isInternalServerError())
                                .andExpect(jsonPath("$.status").value(500))
                                .andExpect(jsonPath("$.message").value(containsString("Failed to retrieve airlines")));
        }

        @Test
        void testGetAirlineByIdSuccess() throws Exception {
                when(airlineService.getAirlineById("GA")).thenReturn(airlineDto1);

                mockMvc.perform(get("/api/airlines/GA"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.status").value(200))
                                .andExpect(jsonPath("$.message").value("Airline retrieved successfully"))
                                .andExpect(jsonPath("$.data.id").value("GA"))
                                .andExpect(jsonPath("$.data.name").value("Garuda Indonesia"));

                verify(airlineService).getAirlineById("GA");
        }

        @Test
        void testGetAirlineByIdNotFound() throws Exception {
                when(airlineService.getAirlineById("XX"))
                                .thenThrow(new RuntimeException("Airline with ID XX not found"));

                mockMvc.perform(get("/api/airlines/XX"))
                                .andExpect(status().isNotFound())
                                .andExpect(jsonPath("$.status").value(404))
                                .andExpect(jsonPath("$.message").value(containsString("not found")));

                verify(airlineService).getAirlineById("XX");
        }

        @Test
        void testCreateAirlineSuccess() throws Exception {
                when(airlineService.createAirline(any(CreateAirlineDto.class))).thenReturn(airlineDto1);

                mockMvc.perform(post("/api/airlines/create")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(createAirlineDto)))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.status").value(201))
                                .andExpect(jsonPath("$.message").value("Airline created successfully"))
                                .andExpect(jsonPath("$.data").exists());

                verify(airlineService).createAirline(any(CreateAirlineDto.class));
        }

        @Test
        void testCreateAirlineValidationError() throws Exception {
                CreateAirlineDto invalidDto = CreateAirlineDto.builder()
                                .id("")
                                .name("")
                                .country("")
                                .build();

                mockMvc.perform(post("/api/airlines/create")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(invalidDto)))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.status").value(400));
        }

        @Test
        void testCreateAirlineAlreadyExists() throws Exception {
                when(airlineService.createAirline(any(CreateAirlineDto.class)))
                                .thenThrow(new RuntimeException("Airline with ID SQ already exists"));

                mockMvc.perform(post("/api/airlines/create")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(createAirlineDto)))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.status").value(400))
                                .andExpect(jsonPath("$.message").value(containsString("already exists")));
        }

        @Test
        void testUpdateAirlineSuccess() throws Exception {
                when(airlineService.updateAirline(any(UpdateAirlineDto.class))).thenReturn(airlineDto1);

                mockMvc.perform(put("/api/airlines/GA/update")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(updateAirlineDto)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.status").value(200))
                                .andExpect(jsonPath("$.message").value("Airline updated successfully"))
                                .andExpect(jsonPath("$.data").exists());

                verify(airlineService).updateAirline(any(UpdateAirlineDto.class));
        }

        @Test
        void testUpdateAirlineNotFound() throws Exception {
                when(airlineService.updateAirline(any(UpdateAirlineDto.class)))
                                .thenThrow(new RuntimeException("Airline with ID XX not found"));

                UpdateAirlineDto dto = UpdateAirlineDto.builder()
                                .id("XX")
                                .name("Test")
                                .country("Test")
                                .build();

                mockMvc.perform(put("/api/airlines/XX/update")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(dto)))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.status").value(400))
                                .andExpect(jsonPath("$.message").value(containsString("not found")));
        }

        @Test
        void testDeleteAirlineSuccess() throws Exception {
                doNothing().when(airlineService).deleteAirline("GA");

                mockMvc.perform(delete("/api/airlines/GA/delete"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.status").value(200))
                                .andExpect(jsonPath("$.message").value(containsString("deleted successfully")));

                verify(airlineService).deleteAirline("GA");
        }

        @Test
        void testDeleteAirlineNotFound() throws Exception {
                doThrow(new RuntimeException("Airline with ID XX not found"))
                                .when(airlineService).deleteAirline("XX");

                mockMvc.perform(delete("/api/airlines/XX/delete"))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.status").value(400))
                                .andExpect(jsonPath("$.message").value(containsString("not found")));
        }

        @Test
        void testSearchAirlines() throws Exception {
                List<ReadAirlineDto> airlines = Arrays.asList(airlineDto1);
                when(airlineService.searchAirlinesByName("Garuda")).thenReturn(airlines);

                mockMvc.perform(get("/api/airlines/search")
                                .param("name", "Garuda"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.status").value(200))
                                .andExpect(jsonPath("$.message")
                                                .value("Airlines search results retrieved successfully"))
                                .andExpect(jsonPath("$.data", hasSize(1)));

                verify(airlineService).searchAirlinesByName("Garuda");
        }

        @Test
        void testGetDistinctCountries() throws Exception {
                List<String> countries = Arrays.asList("Indonesia", "Malaysia", "Singapore");
                when(airlineService.getDistinctCountries()).thenReturn(countries);

                mockMvc.perform(get("/api/airlines/countries"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.status").value(200))
                                .andExpect(jsonPath("$.message").value("Countries retrieved successfully"))
                                .andExpect(jsonPath("$.data", hasSize(3)))
                                .andExpect(jsonPath("$.data[0]").value("Indonesia"));

                verify(airlineService).getDistinctCountries();
        }

        @Test
        void testGetAllAirlinesCheckedException() throws Exception {
                when(airlineService.getAllAirlines()).thenAnswer(inv -> {
                        throw new Exception("checked DB");
                });

                mockMvc.perform(get("/api/airlines/all"))
                                .andExpect(status().isInternalServerError())
                                .andExpect(jsonPath("$.status").value(500));

                verify(airlineService).getAllAirlines();
        }

        @Test
        void testGetAirlineByIdCheckedException() throws Exception {
                when(airlineService.getAirlineById("GA")).thenAnswer(inv -> {
                        throw new Exception("checked unexpected");
                });

                mockMvc.perform(get("/api/airlines/GA"))
                                .andExpect(status().isInternalServerError())
                                .andExpect(jsonPath("$.status").value(500));

                verify(airlineService).getAirlineById("GA");
        }

        @Test
        void testCreateAirlineCheckedException() throws Exception {
                when(airlineService.createAirline(any(CreateAirlineDto.class))).thenAnswer(inv -> {
                        throw new Exception("checked create failed");
                });

                mockMvc.perform(post("/api/airlines/create")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(createAirlineDto)))
                                .andExpect(status().isInternalServerError())
                                .andExpect(jsonPath("$.status").value(500));

                verify(airlineService).createAirline(any(CreateAirlineDto.class));
        }

        @Test
        void testUpdateAirlineCheckedException() throws Exception {
                when(airlineService.updateAirline(any(UpdateAirlineDto.class))).thenAnswer(inv -> {
                        throw new Exception("checked update failed");
                });

                mockMvc.perform(put("/api/airlines/GA/update")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(updateAirlineDto)))
                                .andExpect(status().isInternalServerError())
                                .andExpect(jsonPath("$.status").value(500));

                verify(airlineService).updateAirline(any(UpdateAirlineDto.class));
        }

        @Test
        void testDeleteAirlineCheckedException() throws Exception {
                org.mockito.Mockito.doAnswer(inv -> {
                        throw new Exception("checked delete failed");
                }).when(airlineService).deleteAirline("GA");

                mockMvc.perform(delete("/api/airlines/GA/delete"))
                                .andExpect(status().isInternalServerError())
                                .andExpect(jsonPath("$.status").value(500));

                verify(airlineService).deleteAirline("GA");
        }

        @Test
        void testSearchAirlinesRuntimeException() throws Exception {
                when(airlineService.searchAirlinesByName("X")).thenThrow(new RuntimeException("search failed"));

                mockMvc.perform(get("/api/airlines/search").param("name", "X"))
                                .andExpect(status().isInternalServerError())
                                .andExpect(jsonPath("$.status").value(500));

                verify(airlineService).searchAirlinesByName("X");
        }

        @Test
        void testSearchAirlinesCheckedException() throws Exception {
                when(airlineService.searchAirlinesByName("X")).thenAnswer(inv -> {
                        throw new Exception("checked search failed");
                });

                mockMvc.perform(get("/api/airlines/search").param("name", "X"))
                                .andExpect(status().isInternalServerError())
                                .andExpect(jsonPath("$.status").value(500));

                verify(airlineService).searchAirlinesByName("X");
        }

        @Test
        void testGetDistinctCountriesRuntimeException() throws Exception {
                when(airlineService.getDistinctCountries()).thenThrow(new RuntimeException("countries failed"));

                mockMvc.perform(get("/api/airlines/countries"))
                                .andExpect(status().isInternalServerError())
                                .andExpect(jsonPath("$.status").value(500));

                verify(airlineService).getDistinctCountries();
        }

        @Test
        void testGetDistinctCountriesCheckedException() throws Exception {
                when(airlineService.getDistinctCountries()).thenAnswer(inv -> {
                        throw new Exception("checked countries failed");
                });

                mockMvc.perform(get("/api/airlines/countries"))
                                .andExpect(status().isInternalServerError())
                                .andExpect(jsonPath("$.status").value(500));

                verify(airlineService).getDistinctCountries();
        }
}
