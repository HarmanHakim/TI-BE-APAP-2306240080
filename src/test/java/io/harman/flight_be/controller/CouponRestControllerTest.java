package io.harman.flight_be.controller;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.harman.flight_be.dto.coupon.CouponResponseDTO;
import io.harman.flight_be.dto.coupon.CreateCouponRequestDTO;
import io.harman.flight_be.dto.coupon.UpdateCouponRequestDTO;
import io.harman.flight_be.service.CouponService;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
@SuppressWarnings("removal")
class CouponRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CouponService couponService;

    private UUID couponId;
    private CouponResponseDTO couponResponse;

    @BeforeEach
    void setUp() {
        couponId = UUID.randomUUID();
        couponResponse = CouponResponseDTO.builder()
                .id(couponId)
                .name("Special End Year Coupons")
                .description("Diskon 10% untuk semua booking")
                .points(500)
                .percentOff(10)
                .build();
    }

    @Test
    void testGetCoupons() throws Exception {
        when(couponService.getCoupons()).thenReturn(List.of(couponResponse));

        mockMvc.perform(get("/api/coupons"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(1)))
                .andExpect(jsonPath("$.data[0].name", equalTo("Special End Year Coupons")));
    }

    @Test
    void testGetCouponNotFound() throws Exception {
        when(couponService.getCoupon(couponId)).thenThrow(new IllegalArgumentException("Coupon not found"));

        mockMvc.perform(get("/api/coupons/{id}", couponId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", equalTo("Coupon not found")));
    }

    @Test
    void testCreateCouponSuccess() throws Exception {
        CreateCouponRequestDTO request = CreateCouponRequestDTO.builder()
                .name("New Coupon")
                .description("Diskon 5%")
                .points(200)
                .percentOff(5)
                .build();

        when(couponService.createCoupon(any(CreateCouponRequestDTO.class))).thenReturn(couponResponse);

        mockMvc.perform(post("/api/coupons")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.name", equalTo("Special End Year Coupons")));
    }

    @Test
    void testUpdateCouponSuccess() throws Exception {
        UpdateCouponRequestDTO request = UpdateCouponRequestDTO.builder()
                .name("Special End Year Coupons")
                .description("Diskon 10% untuk semua booking")
                .points(500)
                .percentOff(10)
                .build();

        when(couponService.updateCoupon(any(UUID.class), any(UpdateCouponRequestDTO.class))).thenReturn(couponResponse);

        mockMvc.perform(put("/api/coupons/{id}", couponId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id", equalTo(couponId.toString())));
    }
}
