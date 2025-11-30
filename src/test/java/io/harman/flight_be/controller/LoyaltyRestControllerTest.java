package io.harman.flight_be.controller;

import static org.hamcrest.Matchers.containsString;
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
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
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
@AutoConfigureMockMvc
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
        @WithMockUser(authorities = { "CUSTOMER" })
        void testGetAvailableCoupons() throws Exception {
                CouponResponseDTO couponResponse = CouponResponseDTO.builder()
                                .id(couponId)
                                .name("Special End Year Coupons")
                                .points(500)
                                .percentOff(10)
                                .build();
                when(couponService.getCoupons()).thenReturn(List.of(couponResponse));

                mockMvc.perform(get("/api/loyalty/coupons"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.data", hasSize(1)))
                                .andExpect(jsonPath("$.data[0].name", equalTo("Special End Year Coupons")));
        }

        @Test
        @WithMockUser(authorities = { "CUSTOMER" })
        void testGetDashboard() throws Exception {
                LoyaltyDashboardResponseDTO dashboardResponse = LoyaltyDashboardResponseDTO.builder()
                                .customerId(customerId)
                                .activeCoupons(1)
                                .redeemedCoupons(0)
                                .availableCouponCount(2)
                                .totalPurchasedCoupons(1)
                                .build();

                when(loyaltyService.getDashboard(customerId)).thenReturn(dashboardResponse);

                mockMvc.perform(get("/api/loyalty/dashboard/{userId}", customerId))
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
                                .header("X-API-KEY", "default-loyalty-api-key-change-me")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.data.customerId", equalTo(customerId.toString())));
        }

        @Test
        @WithMockUser(authorities = { "CUSTOMER" })
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
                                .andExpect(status().isOk())
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
                                .header("X-API-KEY", "default-loyalty-api-key-change-me")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.data.percentOff", equalTo(10)));
        }

        @Test
        @WithMockUser(authorities = { "CUSTOMER" })
        void testGetBalance() throws Exception {
                LoyaltyPointsResponseDTO balanceResponse = LoyaltyPointsResponseDTO.builder()
                                .customerId(customerId)
                                .points(500)
                                .build();

                when(loyaltyService.getBalance(customerId)).thenReturn(balanceResponse);

                mockMvc.perform(get("/api/loyalty/balance/{userId}", customerId))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.data.customerId", equalTo(customerId.toString())))
                                .andExpect(jsonPath("$.data.points", equalTo(500)));
        }

        @Test
        @WithMockUser(authorities = { "CUSTOMER" })
        void testGetPurchasedCoupons() throws Exception {
                PurchasedCouponResponseDTO purchasedCoupon = PurchasedCouponResponseDTO.builder()
                                .id(UUID.randomUUID())
                                .couponId(couponId)
                                .customerId(customerId)
                                .code("SPECI-AHMAD-1")
                                .percentOff(10)
                                .build();

                when(loyaltyService.getPurchasedCoupons(customerId)).thenReturn(List.of(purchasedCoupon));

                mockMvc.perform(get("/api/loyalty/coupons/purchased/{userId}", customerId))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.data", hasSize(1)))
                                .andExpect(jsonPath("$.data[0].code", equalTo("SPECI-AHMAD-1")));
        }

        @Test
        void testAddPointsValidationError() throws Exception {
                AddPointsRequestDTO invalidRequest = AddPointsRequestDTO.builder()
                                .points(-100) // Invalid points
                                .build();

                mockMvc.perform(post("/api/loyalty/points")
                                .header("X-API-KEY", "default-loyalty-api-key-change-me")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(invalidRequest)))
                                .andExpect(status().isBadRequest());
        }

        @Test
        @WithMockUser(authorities = { "CUSTOMER" })
        void testPurchaseCouponValidationError() throws Exception {
                PurchaseCouponRequestDTO invalidRequest = PurchaseCouponRequestDTO.builder()
                                .build(); // Missing required fields

                mockMvc.perform(post("/api/loyalty/coupons/purchase")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(invalidRequest)))
                                .andExpect(status().isBadRequest());
        }

        @Test
        void testRedeemCouponValidationError() throws Exception {
                RedeemCouponRequestDTO invalidRequest = RedeemCouponRequestDTO.builder()
                                .build(); // Missing required fields

                mockMvc.perform(post("/api/loyalty/coupons/redeem")
                                .header("X-API-KEY", "default-loyalty-api-key-change-me")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(invalidRequest)))
                                .andExpect(status().isBadRequest());
        }

        @Test
        @WithMockUser(authorities = { "CUSTOMER" })
        void testPurchaseCouponInsufficientPoints() throws Exception {
                PurchaseCouponRequestDTO request = PurchaseCouponRequestDTO.builder()
                                .customerId(customerId)
                                .couponId(couponId)
                                .build();

                when(loyaltyService.purchaseCoupon(any(PurchaseCouponRequestDTO.class)))
                                .thenThrow(new IllegalArgumentException("Insufficient points"));

                mockMvc.perform(post("/api/loyalty/coupons/purchase")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.message", equalTo("Insufficient points")));
        }

        @Test
        void testRedeemCouponInvalidCode() throws Exception {
                RedeemCouponRequestDTO request = RedeemCouponRequestDTO.builder()
                                .code("INVALID-CODE")
                                .customerId(customerId)
                                .build();

                when(loyaltyService.redeemCoupon(any(RedeemCouponRequestDTO.class)))
                                .thenThrow(new IllegalArgumentException("Invalid coupon code"));

                mockMvc.perform(post("/api/loyalty/coupons/redeem")
                                .header("X-API-KEY", "default-loyalty-api-key-change-me")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.message", equalTo("Invalid coupon code")));
        }

        @Test
        @WithMockUser(authorities = { "CUSTOMER" })
        void testGetAvailableCouponsServerError() throws Exception {
                when(couponService.getCoupons()).thenThrow(new RuntimeException("Database error"));

                mockMvc.perform(get("/api/loyalty/coupons"))
                                .andExpect(status().isInternalServerError())
                                .andExpect(jsonPath("$.message", containsString("Failed to retrieve coupons")));
        }

        @Test
        void testAddPointsServerError() throws Exception {
                AddPointsRequestDTO request = AddPointsRequestDTO.builder()
                                .customerId(customerId)
                                .points(100)
                                .build();

                when(loyaltyService.addPoints(any(AddPointsRequestDTO.class)))
                                .thenThrow(new RuntimeException("Database error"));

                mockMvc.perform(post("/api/loyalty/points")
                                .header("X-API-KEY", "default-loyalty-api-key-change-me")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isInternalServerError())
                                .andExpect(jsonPath("$.message", containsString("Failed to add points")));
        }

        @Test
        @WithMockUser(authorities = { "CUSTOMER" })
        void testGetBalanceServerError() throws Exception {
                when(loyaltyService.getBalance(customerId)).thenThrow(new RuntimeException("Database error"));

                mockMvc.perform(get("/api/loyalty/balance/{userId}", customerId))
                                .andExpect(status().isInternalServerError())
                                .andExpect(jsonPath("$.message", containsString("Failed to retrieve balance")));
        }

        @Test
        @WithMockUser(authorities = { "CUSTOMER" })
        void testGetDashboardServerError() throws Exception {
                when(loyaltyService.getDashboard(customerId)).thenThrow(new RuntimeException("Database error"));

                mockMvc.perform(get("/api/loyalty/dashboard/{userId}", customerId))
                                .andExpect(status().isInternalServerError())
                                .andExpect(jsonPath("$.message", containsString("Failed to retrieve dashboard")));
        }

        @Test
        @WithMockUser(authorities = { "CUSTOMER" })
        void testPurchaseCouponServerError() throws Exception {
                PurchaseCouponRequestDTO request = PurchaseCouponRequestDTO.builder()
                                .customerId(customerId)
                                .couponId(couponId)
                                .build();

                when(loyaltyService.purchaseCoupon(any(PurchaseCouponRequestDTO.class)))
                                .thenThrow(new RuntimeException("Database error"));

                mockMvc.perform(post("/api/loyalty/coupons/purchase")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isInternalServerError())
                                .andExpect(jsonPath("$.message", containsString("Failed to purchase coupon")));
        }

        @Test
        @WithMockUser(authorities = { "CUSTOMER" })
        void testGetPurchasedCouponsServerError() throws Exception {
                when(loyaltyService.getPurchasedCoupons(customerId)).thenThrow(new RuntimeException("Database error"));

                mockMvc.perform(get("/api/loyalty/coupons/purchased/{userId}", customerId))
                                .andExpect(status().isInternalServerError())
                                .andExpect(jsonPath("$.message",
                                                containsString("Failed to retrieve purchased coupons")));
        }

        @Test
        void testRedeemCouponServerError() throws Exception {
                RedeemCouponRequestDTO request = RedeemCouponRequestDTO.builder()
                                .code("SPECI-AHMAD-1")
                                .customerId(customerId)
                                .build();

                when(loyaltyService.redeemCoupon(any(RedeemCouponRequestDTO.class)))
                                .thenThrow(new RuntimeException("Database error"));

                mockMvc.perform(post("/api/loyalty/coupons/redeem")
                                .header("X-API-KEY", "default-loyalty-api-key-change-me")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isInternalServerError())
                                .andExpect(jsonPath("$.message", containsString("Failed to redeem coupon")));
        }
}
