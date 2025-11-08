package io.harman.flight_be.controller;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import io.harman.flight_be.service.HomeService;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class HomeRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private HomeService homeService;

    @Test
    void testGetHomeStatisticsSuccess() throws Exception {
        Map<String, Object> stats = Map.of(
                "activeFlightsCount", 5,
                "bookingsCount", 12,
                "airlinesCount", 3);

        when(homeService.getHomeStatistics()).thenReturn(stats);

        mockMvc.perform(get("/api/home").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is(200)))
                .andExpect(jsonPath("$.data.activeFlightsCount", is(5)))
                .andExpect(jsonPath("$.data.bookingsCount", is(12)))
                .andExpect(jsonPath("$.data.airlinesCount", is(3)));

        verify(homeService).getHomeStatistics();
    }

    @Test
    void testGetHomeStatisticsServerError() throws Exception {
        when(homeService.getHomeStatistics()).thenThrow(new RuntimeException("DB down"));

        mockMvc.perform(get("/api/home").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status", is(500)));

        verify(homeService).getHomeStatistics();
    }
}
