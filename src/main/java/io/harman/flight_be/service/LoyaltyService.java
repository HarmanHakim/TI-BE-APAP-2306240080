package io.harman.flight_be.service;

import java.util.List;
import java.util.UUID;

import io.harman.flight_be.dto.loyalty.AddPointsRequestDTO;
import io.harman.flight_be.dto.loyalty.LoyaltyDashboardResponseDTO;
import io.harman.flight_be.dto.loyalty.LoyaltyPointsResponseDTO;
import io.harman.flight_be.dto.loyalty.PurchaseCouponRequestDTO;
import io.harman.flight_be.dto.loyalty.PurchasedCouponResponseDTO;
import io.harman.flight_be.dto.loyalty.RedeemCouponRequestDTO;
import io.harman.flight_be.dto.loyalty.RedeemCouponResponseDTO;

public interface LoyaltyService {

    LoyaltyPointsResponseDTO addPoints(AddPointsRequestDTO request);

    LoyaltyPointsResponseDTO getBalance(UUID customerId);

    PurchasedCouponResponseDTO purchaseCoupon(PurchaseCouponRequestDTO request);

    List<PurchasedCouponResponseDTO> getPurchasedCoupons(UUID customerId);

    LoyaltyDashboardResponseDTO getDashboard(UUID customerId);

    RedeemCouponResponseDTO redeemCoupon(RedeemCouponRequestDTO request);
}
