package io.harman.flight_be.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.harman.flight_be.dto.passenger.CreatePassengerDto;
import io.harman.flight_be.dto.passenger.ReadPassengerDto;
import io.harman.flight_be.dto.passenger.UpdatePassengerDto;
import io.harman.flight_be.service.PassengerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
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
class PassengerRestControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @Autowired
        private ObjectMapper objectMapper;

        @MockBean
        private PassengerService passengerService;

        private ReadPassengerDto passengerDto1;
        private ReadPassengerDto passengerDto2;
        private CreatePassengerDto createPassengerDto;
        private UpdatePassengerDto updatePassengerDto;
        private UUID passengerId;

        @BeforeEach
        void setUp() {
                passengerId = UUID.randomUUID();

                passengerDto1 = ReadPassengerDto.builder()
                                .id(passengerId)
                                .fullName("John Doe")
                                .birthDate(LocalDate.of(1990, 1, 1))
                                .age(34)
                                .gender(1)
                                .genderLabel("Male")
                                .idPassport("A1234567")
                                .createdAt(LocalDateTime.now())
                                .updatedAt(LocalDateTime.now())
                                .build();

                passengerDto2 = ReadPassengerDto.builder()
                                .id(UUID.randomUUID())
                                .fullName("Jane Smith")
                                .birthDate(LocalDate.of(1995, 5, 15))
                                .age(29)
                                .gender(2)
                                .genderLabel("Female")
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
                                .fullName("John Doe Updated")
                                .birthDate(LocalDate.of(1990, 1, 1))
                                .gender(1)
                                .idPassport("A1234567")
                                .build();
        }

        @Test
        void testGetAllPassengers() throws Exception {
                List<ReadPassengerDto> passengers = Arrays.asList(passengerDto1, passengerDto2);
                when(passengerService.getAllPassengers()).thenReturn(passengers);

                mockMvc.perform(get("/api/passengers/all"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.status").value(200))
                                .andExpect(jsonPath("$.message").value("Passengers retrieved successfully"))
                                .andExpect(jsonPath("$.data", hasSize(2)))
                                .andExpect(jsonPath("$.data[0].fullName").value("John Doe"));

                verify(passengerService).getAllPassengers();
        }

        @Test
        void testGetAllPassengersByGender() throws Exception {
                List<ReadPassengerDto> passengers = Arrays.asList(passengerDto1);
                when(passengerService.getPassengersByGender(1)).thenReturn(passengers);

                mockMvc.perform(get("/api/passengers/all")
                                .param("gender", "1"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.status").value(200))
                                .andExpect(jsonPath("$.data", hasSize(1)))
                                .andExpect(jsonPath("$.data[0].gender").value(1));

                verify(passengerService).getPassengersByGender(1);
        }

        @Test
        void testGetPassengerByIdSuccess() throws Exception {
                when(passengerService.getPassengerById(passengerId)).thenReturn(passengerDto1);

                mockMvc.perform(get("/api/passengers/" + passengerId))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.status").value(200))
                                .andExpect(jsonPath("$.message").value("Passenger retrieved successfully"))
                                .andExpect(jsonPath("$.data.fullName").value("John Doe"))
                                .andExpect(jsonPath("$.data.idPassport").value("A1234567"));

                verify(passengerService).getPassengerById(passengerId);
        }

        @Test
        void testGetPassengerByIdNotFound() throws Exception {
                UUID fakeId = UUID.randomUUID();
                when(passengerService.getPassengerById(fakeId))
                                .thenThrow(new RuntimeException("Passenger with ID " + fakeId + " not found"));

                mockMvc.perform(get("/api/passengers/" + fakeId))
                                .andExpect(status().isNotFound())
                                .andExpect(jsonPath("$.status").value(404))
                                .andExpect(jsonPath("$.message").value(containsString("not found")));

                verify(passengerService).getPassengerById(fakeId);
        }

        @Test
        void testCreatePassengerSuccess() throws Exception {
                when(passengerService.createPassenger(any(CreatePassengerDto.class))).thenReturn(passengerDto1);

                mockMvc.perform(post("/api/passengers/create")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(createPassengerDto)))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.status").value(201))
                                .andExpect(jsonPath("$.message").value("Passenger created successfully"))
                                .andExpect(jsonPath("$.data").exists());

                verify(passengerService).createPassenger(any(CreatePassengerDto.class));
        }

        @Test
        void testCreatePassengerValidationError() throws Exception {
                CreatePassengerDto invalidDto = CreatePassengerDto.builder()
                                .fullName("")
                                .birthDate(null)
                                .gender(null)
                                .idPassport("")
                                .build();

                mockMvc.perform(post("/api/passengers/create")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(invalidDto)))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.status").value(400));
        }

        @Test
        void testCreatePassengerAlreadyExists() throws Exception {
                when(passengerService.createPassenger(any(CreatePassengerDto.class)))
                                .thenThrow(new RuntimeException("Passenger with ID/Passport already exists"));

                mockMvc.perform(post("/api/passengers/create")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(createPassengerDto)))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.status").value(400))
                                .andExpect(jsonPath("$.message").value(containsString("already exists")));
        }

        @Test
        void testUpdatePassengerSuccess() throws Exception {
                when(passengerService.updatePassenger(any(UpdatePassengerDto.class))).thenReturn(passengerDto1);

                mockMvc.perform(put("/api/passengers/" + passengerId + "/update")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(updatePassengerDto)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.status").value(200))
                                .andExpect(jsonPath("$.message").value("Passenger updated successfully"))
                                .andExpect(jsonPath("$.data").exists());

                verify(passengerService).updatePassenger(any(UpdatePassengerDto.class));
        }

        @Test
        void testUpdatePassengerNotFound() throws Exception {
                UUID fakeId = UUID.randomUUID();
                when(passengerService.updatePassenger(any(UpdatePassengerDto.class)))
                                .thenThrow(new RuntimeException("Passenger with ID " + fakeId + " not found"));

                UpdatePassengerDto dto = UpdatePassengerDto.builder()
                                .id(fakeId)
                                .fullName("Test")
                                .birthDate(LocalDate.now())
                                .gender(1)
                                .idPassport("TEST")
                                .build();

                mockMvc.perform(put("/api/passengers/" + fakeId + "/update")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(dto)))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.status").value(400))
                                .andExpect(jsonPath("$.message").value(containsString("not found")));
        }

        @Test
        void testDeletePassengerSuccess() throws Exception {
                doNothing().when(passengerService).deletePassenger(passengerId);

                mockMvc.perform(delete("/api/passengers/" + passengerId + "/delete"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.status").value(200))
                                .andExpect(jsonPath("$.message").value(containsString("deleted successfully")));

                verify(passengerService).deletePassenger(passengerId);
        }

        @Test
        void testDeletePassengerNotFound() throws Exception {
                UUID fakeId = UUID.randomUUID();
                doThrow(new RuntimeException("Passenger with ID " + fakeId + " not found"))
                                .when(passengerService).deletePassenger(fakeId);

                mockMvc.perform(delete("/api/passengers/" + fakeId + "/delete"))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.status").value(400))
                                .andExpect(jsonPath("$.message").value(containsString("not found")));
        }

        @Test
        void testGetAllPassengersException() throws Exception {
                when(passengerService.getAllPassengers()).thenThrow(new RuntimeException("Database error"));

                mockMvc.perform(get("/api/passengers/all"))
                                .andExpect(status().isInternalServerError())
                                .andExpect(jsonPath("$.status").value(500))
                                .andExpect(jsonPath("$.message")
                                                .value(containsString("Failed to retrieve passengers")));
        }
}
