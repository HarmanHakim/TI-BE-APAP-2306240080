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

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

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

import io.harman.flight_be.dto.booking.CreateBookingDto;
import io.harman.flight_be.dto.booking.ReadBookingDto;
import io.harman.flight_be.dto.booking.UpdateBookingDto;
import io.harman.flight_be.service.BookingService;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@WithMockUser(authorities = {"SUPERADMIN"})
class BookingRestControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @Autowired
        private ObjectMapper objectMapper;

        @MockBean
        private BookingService bookingService;

        private ReadBookingDto bookingDto1;
        private ReadBookingDto bookingDto2;
        private CreateBookingDto createBookingDto;
        private UpdateBookingDto updateBookingDto;

        @BeforeEach
        void setUp() {
                bookingDto1 = ReadBookingDto.builder()
                                .id("BK001")
                                .flightId("FL001")
                                .classFlightId(1)
                                .contactEmail("john@example.com")
                                .contactPhone("08123456789")
                                .passengerCount(1)
                                .status(1)
                                .totalPrice(new BigDecimal("1500000"))
                                .createdAt(LocalDateTime.now())
                                .build();

                bookingDto2 = ReadBookingDto.builder()
                                .id("BK002")
                                .flightId("FL002")
                                .classFlightId(2)
                                .contactEmail("jane@example.com")
                                .contactPhone("08198765432")
                                .passengerCount(2)
                                .status(2)
                                .totalPrice(new BigDecimal("3000000"))
                                .createdAt(LocalDateTime.now())
                                .build();

                createBookingDto = CreateBookingDto.builder()
                                .flightId("FL001")
                                .classFlightId(1)
                                .contactEmail("test@example.com")
                                .contactPhone("08111111111")
                                .passengerCount(1)
                                .status(1)
                                .totalPrice(new BigDecimal("1500000"))
                                .passengerIds(Arrays.asList(UUID.randomUUID()))
                                .build();

                updateBookingDto = UpdateBookingDto.builder()
                                .flightId("FL001")
                                .classFlightId(1)
                                .contactEmail("updated@example.com")
                                .contactPhone("08222222222")
                                .passengerCount(1)
                                .status(2)
                                .totalPrice(new BigDecimal("1600000"))
                                .build();
        }

        @Test
        void testGetAllBookings() throws Exception {
                List<ReadBookingDto> bookings = Arrays.asList(bookingDto1, bookingDto2);
                when(bookingService.getAllBookings()).thenReturn(bookings);

                mockMvc.perform(get("/api/bookings"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.status").value(200))
                                .andExpect(jsonPath("$.message").value("Bookings retrieved successfully"))
                                .andExpect(jsonPath("$.data", hasSize(2)));

                verify(bookingService).getAllBookings();
        }

        @Test
        void testGetAllBookingsByStatus() throws Exception {
                List<ReadBookingDto> bookings = Arrays.asList(bookingDto1);
                when(bookingService.getBookingsByStatus(1)).thenReturn(bookings);

                mockMvc.perform(get("/api/bookings")
                                .param("status", "1"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.status").value(200))
                                .andExpect(jsonPath("$.data", hasSize(1)));

                verify(bookingService).getBookingsByStatus(1);
        }

        @Test
        void testGetAllBookingsByEmail() throws Exception {
                List<ReadBookingDto> bookings = Arrays.asList(bookingDto1);
                when(bookingService.getBookingsByEmail("john@example.com")).thenReturn(bookings);

                mockMvc.perform(get("/api/bookings")
                                .param("email", "john@example.com"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.status").value(200))
                                .andExpect(jsonPath("$.data", hasSize(1)));

                verify(bookingService).getBookingsByEmail("john@example.com");
        }

        @Test
        void testGetAllActiveBookings() throws Exception {
                List<ReadBookingDto> bookings = Arrays.asList(bookingDto1, bookingDto2);
                when(bookingService.getAllActiveBookings()).thenReturn(bookings);

                mockMvc.perform(get("/api/bookings")
                                .param("isActive", "true"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.status").value(200))
                                .andExpect(jsonPath("$.data", hasSize(2)));

                verify(bookingService).getAllActiveBookings();
        }

        @Test
        void testGetBookingByIdSuccess() throws Exception {
                when(bookingService.getBookingById("BK001")).thenReturn(bookingDto1);

                mockMvc.perform(get("/api/bookings/BK001"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.status").value(200))
                                .andExpect(jsonPath("$.message").value("Booking retrieved successfully"))
                                .andExpect(jsonPath("$.data.id").value("BK001"));

                verify(bookingService).getBookingById("BK001");
        }

        @Test
        void testGetBookingByIdNotFound() throws Exception {
                when(bookingService.getBookingById("XX"))
                                .thenThrow(new RuntimeException("Booking with ID XX not found"));

                mockMvc.perform(get("/api/bookings/XX"))
                                .andExpect(status().isNotFound())
                                .andExpect(jsonPath("$.status").value(404))
                                .andExpect(jsonPath("$.message").value(containsString("not found")));

                verify(bookingService).getBookingById("XX");
        }

        @Test
        void testCreateBookingSuccess() throws Exception {
                when(bookingService.createBooking(any(CreateBookingDto.class))).thenReturn(bookingDto1);

                mockMvc.perform(post("/api/bookings/create")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(createBookingDto)))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.status").value(201))
                                .andExpect(jsonPath("$.message").value("Booking created successfully"))
                                .andExpect(jsonPath("$.data").exists());

                verify(bookingService).createBooking(any(CreateBookingDto.class));
        }

        @Test
        void testCreateBookingValidationError() throws Exception {
                CreateBookingDto invalidDto = CreateBookingDto.builder()
                                .flightId("")
                                .contactEmail("invalid-email")
                                .contactPhone("")
                                .passengerCount(0)
                                .build();

                mockMvc.perform(post("/api/bookings/create")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(invalidDto)))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.status").value(400));
        }

        @Test
        void testCreateBookingFlightNotFound() throws Exception {
                when(bookingService.createBooking(any(CreateBookingDto.class)))
                                .thenThrow(new RuntimeException("Flight not found"));

                mockMvc.perform(post("/api/bookings/create")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(createBookingDto)))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.status").value(400))
                                .andExpect(jsonPath("$.message").value(containsString("Flight not found")));
        }

        @Test
        void testUpdateBookingSuccess() throws Exception {
                when(bookingService.updateBooking(any(UpdateBookingDto.class))).thenReturn(bookingDto1);

                mockMvc.perform(put("/api/bookings/BK001/update")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(updateBookingDto)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.status").value(200))
                                .andExpect(jsonPath("$.message").value("Booking updated successfully"))
                                .andExpect(jsonPath("$.data").exists());

                verify(bookingService).updateBooking(any(UpdateBookingDto.class));
        }

        @Test
        void testUpdateBookingNotFound() throws Exception {
                when(bookingService.updateBooking(any(UpdateBookingDto.class)))
                                .thenThrow(new RuntimeException("Booking with ID XX not found"));

                mockMvc.perform(put("/api/bookings/XX/update")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(updateBookingDto)))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.status").value(400))
                                .andExpect(jsonPath("$.message").value(containsString("not found")));
        }

        @Test
        void testDeleteBookingSuccess() throws Exception {
                doNothing().when(bookingService).deleteBooking("BK001");

                mockMvc.perform(delete("/api/bookings/BK001/delete"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.status").value(200))
                                .andExpect(jsonPath("$.message").value(containsString("cancelled successfully")));

                verify(bookingService).deleteBooking("BK001");
        }

        @Test
        void testDeleteBookingNotFound() throws Exception {
                doThrow(new RuntimeException("Booking with ID XX not found"))
                                .when(bookingService).deleteBooking("XX");

                mockMvc.perform(delete("/api/bookings/XX/delete"))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.status").value(400))
                                .andExpect(jsonPath("$.message").value(containsString("not found")));
        }

        @Test
        void testGetAllBookingsException() throws Exception {
                when(bookingService.getAllBookings()).thenThrow(new RuntimeException("Database error"));

                mockMvc.perform(get("/api/bookings"))
                                .andExpect(status().isInternalServerError())
                                .andExpect(jsonPath("$.status").value(500))
                                .andExpect(jsonPath("$.message").value(containsString("Failed to retrieve bookings")));
        }

        @Test
        void testGetAllBookingsCheckedException() throws Exception {
                when(bookingService.getAllBookings()).thenAnswer(inv -> {
                        throw new Exception("checked DB");
                });

                mockMvc.perform(get("/api/bookings"))
                                .andExpect(status().isInternalServerError())
                                .andExpect(jsonPath("$.status").value(500));

                verify(bookingService).getAllBookings();
        }

        @Test
        void testGetBookingByIdCheckedException() throws Exception {
                when(bookingService.getBookingById("BK001")).thenAnswer(inv -> {
                        throw new Exception("checked unexpected");
                });

                mockMvc.perform(get("/api/bookings/BK001"))
                                .andExpect(status().isInternalServerError())
                                .andExpect(jsonPath("$.status").value(500));

                verify(bookingService).getBookingById("BK001");
        }

        @Test
        void testCreateBookingCheckedException() throws Exception {
                when(bookingService.createBooking(any(CreateBookingDto.class))).thenAnswer(inv -> {
                        throw new Exception("checked create failed");
                });

                mockMvc.perform(post("/api/bookings/create")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(createBookingDto)))
                                .andExpect(status().isInternalServerError())
                                .andExpect(jsonPath("$.status").value(500));

                verify(bookingService).createBooking(any(CreateBookingDto.class));
        }

        @Test
        void testUpdateBookingCheckedException() throws Exception {
                when(bookingService.updateBooking(any(UpdateBookingDto.class))).thenAnswer(inv -> {
                        throw new Exception("checked update failed");
                });

                mockMvc.perform(put("/api/bookings/BK001/update")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(updateBookingDto)))
                                .andExpect(status().isInternalServerError())
                                .andExpect(jsonPath("$.status").value(500));

                verify(bookingService).updateBooking(any(UpdateBookingDto.class));
        }

        @Test
        void testDeleteBookingCheckedException() throws Exception {
                org.mockito.Mockito.doAnswer(inv -> {
                        throw new Exception("checked delete failed");
                }).when(bookingService).deleteBooking("BK001");

                mockMvc.perform(delete("/api/bookings/BK001/delete"))
                                .andExpect(status().isInternalServerError())
                                .andExpect(jsonPath("$.status").value(500));

                verify(bookingService).deleteBooking("BK001");
        }

        @Test
        void testGetBookingStatisticsSuccess() throws Exception {
                java.time.LocalDateTime start = java.time.LocalDateTime.now().minusDays(7);
                java.time.LocalDateTime end = java.time.LocalDateTime.now();

                when(bookingService.getBookingStatistics(any(java.time.LocalDateTime.class),
                                any(java.time.LocalDateTime.class)))
                                .thenReturn(java.util.Map.of("totalBookings", 5, "totalRevenue", 1500000));

                mockMvc.perform(get("/api/bookings/statistics")
                                .param("start", start.toString())
                                .param("end", end.toString()))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.status").value(200))
                                .andExpect(jsonPath("$.data.totalBookings").value(5));

                verify(bookingService).getBookingStatistics(any(java.time.LocalDateTime.class),
                                any(java.time.LocalDateTime.class));
        }

        @Test
        void testGetBookingStatisticsRuntimeException() throws Exception {
                when(bookingService.getBookingStatistics(any(java.time.LocalDateTime.class),
                                any(java.time.LocalDateTime.class)))
                                .thenThrow(new RuntimeException("stats failure"));

                mockMvc.perform(get("/api/bookings/statistics")
                                .param("start", java.time.LocalDateTime.now().minusDays(1).toString())
                                .param("end", java.time.LocalDateTime.now().toString()))
                                .andExpect(status().isInternalServerError())
                                .andExpect(jsonPath("$.status").value(500));

                verify(bookingService).getBookingStatistics(any(java.time.LocalDateTime.class),
                                any(java.time.LocalDateTime.class));
        }

        @Test
        void testGetBookingStatisticsCheckedException() throws Exception {
                when(bookingService.getBookingStatistics(any(java.time.LocalDateTime.class),
                                any(java.time.LocalDateTime.class)))
                                .thenAnswer(inv -> {
                                        throw new Exception("checked stats failed");
                                });

                mockMvc.perform(get("/api/bookings/statistics")
                                .param("start", java.time.LocalDateTime.now().minusDays(1).toString())
                                .param("end", java.time.LocalDateTime.now().toString()))
                                .andExpect(status().isInternalServerError())
                                .andExpect(jsonPath("$.status").value(500));

                verify(bookingService).getBookingStatistics(any(java.time.LocalDateTime.class),
                                any(java.time.LocalDateTime.class));
        }
}
