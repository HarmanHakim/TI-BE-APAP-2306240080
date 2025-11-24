package io.harman.flight_be.controller;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
import io.harman.flight_be.dto.loyalty.AddPointsRequestDTO;
import io.harman.flight_be.dto.loyalty.LoyaltyDashboardResponseDTO;
import io.harman.flight_be.dto.loyalty.LoyaltyPointsResponseDTO;
import io.harman.flight_be.dto.loyalty.PurchaseCouponRequestDTO;
import io.harman.flight_be.dto.loyalty.PurchasedCouponResponseDTO;
import io.harman.flight_be.dto.loyalty.RedeemCouponRequestDTO;
import io.harman.flight_be.dto.loyalty.RedeemCouponResponseDTO;
import io.harman.flight_be.service.CouponService;
import io.harman.flight_be.service.LoyaltyService;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
@SuppressWarnings("removal")
class LoyaltyRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private LoyaltyService loyaltyService;

    @MockBean
    private CouponService couponService;

    private UUID customerId;
    private UUID couponId;

    @BeforeEach
    void setUp() {
        customerId = UUID.randomUUID();
        couponId = UUID.randomUUID();
    }

    @Test
    void testGetAvailableCoupons() throws Exception {
        CouponResponseDTO couponResponse = CouponResponseDTO.builder()
                .id(couponId)
                .name("Special End Year Coupons")
                .points(500)
                .percentOff(10)
                .build();
        when(couponService.getCoupons()).thenReturn(List.of(couponResponse));

        mockMvc.perform(get("/api/loyalty/coupons/available"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(1)))
                .andExpect(jsonPath("$.data[0].name", equalTo("Special End Year Coupons")));
    }

    @Test
    void testGetDashboard() throws Exception {
        LoyaltyDashboardResponseDTO dashboardResponse = LoyaltyDashboardResponseDTO.builder()
                .customerId(customerId)
                .activeCoupons(1)
                .redeemedCoupons(0)
                .availableCouponCount(2)
                .totalPurchasedCoupons(1)
                .build();

        when(loyaltyService.getDashboard(customerId)).thenReturn(dashboardResponse);

        mockMvc.perform(get("/api/loyalty/customers/{customerId}/dashboard", customerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.activeCoupons", equalTo(1)))
                .andExpect(jsonPath("$.data.availableCouponCount", equalTo(2)));
    }

    @Test
    void testAddPointsSuccess() throws Exception {
        AddPointsRequestDTO request = AddPointsRequestDTO.builder()
                .customerId(customerId)
                .points(100)
                .build();

        LoyaltyPointsResponseDTO responseDTO = LoyaltyPointsResponseDTO.builder()
                .customerId(customerId)
                .points(200)
                .build();

        when(loyaltyService.addPoints(any(AddPointsRequestDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(post("/api/loyalty/points")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.customerId", equalTo(customerId.toString())));
    }

    @Test
    void testPurchaseCouponSuccess() throws Exception {
        PurchaseCouponRequestDTO request = PurchaseCouponRequestDTO.builder()
                .customerId(customerId)
                .couponId(couponId)
                .build();

        PurchasedCouponResponseDTO responseDTO = PurchasedCouponResponseDTO.builder()
                .id(UUID.randomUUID())
                .couponId(couponId)
                .customerId(customerId)
                .code("SPECI-AHMAD-1")
                .percentOff(10)
                .build();

        when(loyaltyService.purchaseCoupon(any(PurchaseCouponRequestDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(post("/api/loyalty/coupons/purchase")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.code", equalTo("SPECI-AHMAD-1")));
    }

    @Test
    void testRedeemCouponSuccess() throws Exception {
        RedeemCouponRequestDTO request = RedeemCouponRequestDTO.builder()
                .code("SPECI-AHMAD-1")
                .customerId(customerId)
                .build();

        RedeemCouponResponseDTO responseDTO = RedeemCouponResponseDTO.builder()
                .code("SPECI-AHMAD-1")
                .customerId(customerId)
                .couponId(couponId)
                .percentOff(10)
                .valid(true)
                .build();

        when(loyaltyService.redeemCoupon(any(RedeemCouponRequestDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(post("/api/loyalty/coupons/redeem")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.percentOff", equalTo(10)));
    }
}
