package io.harman.flight_be.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import io.harman.flight_be.dto.UserProfileDTO;
import io.harman.flight_be.dto.loyalty.AddPointsRequestDTO;
import io.harman.flight_be.dto.loyalty.LoyaltyDashboardResponseDTO;
import io.harman.flight_be.dto.loyalty.LoyaltyPointsResponseDTO;
import io.harman.flight_be.dto.loyalty.PurchaseCouponRequestDTO;
import io.harman.flight_be.dto.loyalty.PurchasedCouponResponseDTO;
import io.harman.flight_be.dto.loyalty.RedeemCouponRequestDTO;
import io.harman.flight_be.dto.loyalty.RedeemCouponResponseDTO;
import io.harman.flight_be.model.loyalty.Coupon;
import io.harman.flight_be.model.loyalty.LoyaltyPoints;
import io.harman.flight_be.model.loyalty.PurchasedCoupon;
import io.harman.flight_be.repository.loyalty.CouponRepository;
import io.harman.flight_be.repository.loyalty.LoyaltyPointsRepository;
import io.harman.flight_be.repository.loyalty.PurchasedCouponRepository;
import io.harman.flight_be.service.LoyaltyServiceImpl;

@ExtendWith(MockitoExtension.class)
class LoyaltyServiceTest {

        @Mock
        private LoyaltyPointsRepository loyaltyPointsRepository;

        @Mock
        private CouponRepository couponRepository;

        @Mock
        private PurchasedCouponRepository purchasedCouponRepository;

        @Mock
        private ExternalApiService externalApiService;

        @InjectMocks
        private LoyaltyServiceImpl loyaltyService;

        private UUID customerId;
        private UUID couponId;

        @BeforeEach
        void setUp() {
                customerId = UUID.randomUUID();
                couponId = UUID.randomUUID();
        }

        @Test
        void addPoints_WithValidApiKey_Succeeds() {
                LoyaltyPoints existing = LoyaltyPoints.builder()
                                .customerId(customerId)
                                .points(100)
                                .build();

                when(loyaltyPointsRepository.findByCustomerId(customerId)).thenReturn(Optional.of(existing));
                when(loyaltyPointsRepository.save(any(LoyaltyPoints.class)))
                                .thenAnswer(invocation -> invocation.getArgument(0));

                AddPointsRequestDTO request = AddPointsRequestDTO.builder()
                                .customerId(customerId)
                                .points(200)
                                .build();

                LoyaltyPointsResponseDTO response = loyaltyService.addPoints(request);
                assertEquals(300, response.getPoints());
        }

        @Test
        void purchaseCoupon_WithEnoughPoints_Succeeds() {
                Coupon coupon = Coupon.builder()
                                .id(couponId)
                                .name("Special End Year Coupons")
                                .points(500)
                                .percentOff(10)
                                .build();

                LoyaltyPoints balance = LoyaltyPoints.builder()
                                .customerId(customerId)
                                .points(600)
                                .build();

                when(couponRepository.findById(couponId)).thenReturn(Optional.of(coupon));
                when(loyaltyPointsRepository.findByCustomerId(customerId)).thenReturn(Optional.of(balance));
                when(loyaltyPointsRepository.save(any(LoyaltyPoints.class)))
                                .thenAnswer(invocation -> invocation.getArgument(0));
                when(purchasedCouponRepository.countByCustomerIdAndCouponId(customerId, couponId)).thenReturn(0L);
                when(purchasedCouponRepository.existsByCode(anyString())).thenReturn(false);
                when(externalApiService.getUserProfile(customerId)).thenReturn(
                                UserProfileDTO.builder().id(customerId).name("Ahmad").build());
                when(purchasedCouponRepository.save(any(PurchasedCoupon.class)))
                                .thenAnswer(invocation -> {
                                        PurchasedCoupon purchased = invocation.getArgument(0);
                                        purchased.setId(UUID.randomUUID());
                                        purchased.setPurchasedDate(LocalDateTime.now());
                                        return purchased;
                                });

                PurchaseCouponRequestDTO request = PurchaseCouponRequestDTO.builder()
                                .couponId(couponId)
                                .customerId(customerId)
                                .build();

                PurchasedCouponResponseDTO response = loyaltyService.purchaseCoupon(request);
                assertEquals(customerId, response.getCustomerId());
                assertEquals(couponId, response.getCouponId());
                assertEquals(10, response.getPercentOff());
        }

        @Test
        void purchaseCoupon_WithInsufficientPoints_ThrowsException() {
                Coupon coupon = Coupon.builder()
                                .id(couponId)
                                .name("Special")
                                .points(500)
                                .percentOff(10)
                                .build();

                LoyaltyPoints balance = LoyaltyPoints.builder()
                                .customerId(customerId)
                                .points(100)
                                .build();

                when(couponRepository.findById(couponId)).thenReturn(Optional.of(coupon));
                when(loyaltyPointsRepository.findByCustomerId(customerId)).thenReturn(Optional.of(balance));

                PurchaseCouponRequestDTO request = PurchaseCouponRequestDTO.builder()
                                .couponId(couponId)
                                .customerId(customerId)
                                .build();

                assertThrows(IllegalStateException.class, () -> loyaltyService.purchaseCoupon(request));
        }

        @Test
        void redeemCoupon_WithValidApiKey_Succeeds() {
                Coupon coupon = Coupon.builder()
                                .id(couponId)
                                .name("Special")
                                .points(500)
                                .percentOff(10)
                                .build();

                PurchasedCoupon purchasedCoupon = PurchasedCoupon.builder()
                                .id(UUID.randomUUID())
                                .couponId(coupon.getId())
                                .customerId(customerId)
                                .code("SPECI-AHMAD-1")
                                .purchasedDate(LocalDateTime.now().minusDays(1))
                                .build();

                when(purchasedCouponRepository.findByCode("SPECI-AHMAD-1"))
                                .thenReturn(Optional.of(purchasedCoupon));
                when(purchasedCouponRepository.save(any(PurchasedCoupon.class)))
                                .thenAnswer(invocation -> invocation.getArgument(0));
                when(couponRepository.findById(couponId)).thenReturn(Optional.of(coupon));

                RedeemCouponRequestDTO request = RedeemCouponRequestDTO.builder()
                                .code("SPECI-AHMAD-1")
                                .customerId(customerId)
                                .build();

                RedeemCouponResponseDTO response = loyaltyService.redeemCoupon(request);
                assertEquals(10, response.getPercentOff());
                assertEquals(customerId, response.getCustomerId());
        }

        @Test
        void getDashboard_AggregatesData() {
                LoyaltyPoints loyaltyPoints = LoyaltyPoints.builder()
                                .customerId(customerId)
                                .points(450)
                                .build();

                Coupon coupon = Coupon.builder()
                                .id(couponId)
                                .name("Winter Deal")
                                .description("15% off winter flights")
                                .points(150)
                                .percentOff(15)
                                .build();

                PurchasedCoupon purchasedCoupon = PurchasedCoupon.builder()
                                .id(UUID.randomUUID())
                                .couponId(couponId)
                                .customerId(customerId)
                                .code("WINTER-JOHN-1")
                                .build();

                when(loyaltyPointsRepository.findByCustomerId(customerId)).thenReturn(Optional.of(loyaltyPoints));
                when(purchasedCouponRepository.findByCustomerIdOrderByPurchasedDateDesc(customerId))
                                .thenReturn(List.of(purchasedCoupon));
                when(couponRepository.findById(couponId)).thenReturn(Optional.of(coupon));
                when(couponRepository.findAllByOrderByCreatedDateDesc()).thenReturn(List.of(coupon));
                when(purchasedCouponRepository.countByCustomerId(customerId)).thenReturn(1L);
                when(purchasedCouponRepository.countByCustomerIdAndUsedDateIsNull(customerId)).thenReturn(1L);
                when(purchasedCouponRepository.countByCustomerIdAndUsedDateIsNotNull(customerId)).thenReturn(0L);

                LoyaltyDashboardResponseDTO dashboard = loyaltyService.getDashboard(customerId);

                assertEquals(customerId, dashboard.getCustomerId());
                assertEquals(450, dashboard.getBalance().getPoints());
                assertEquals(1, dashboard.getTotalPurchasedCoupons());
                assertEquals(1, dashboard.getActiveCoupons());
                assertEquals(0, dashboard.getRedeemedCoupons());
                assertEquals(1, dashboard.getAvailableCouponCount());
                assertEquals("WINTER-JOHN-1", dashboard.getPurchasedCoupons().get(0).getCode());
                assertEquals("Winter Deal", dashboard.getAvailableCoupons().get(0).getName());
        }
}
