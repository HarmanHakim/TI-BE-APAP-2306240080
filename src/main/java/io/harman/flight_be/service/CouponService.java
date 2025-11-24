package io.harman.flight_be.service;

import java.util.List;
import java.util.UUID;

import io.harman.flight_be.dto.coupon.CouponResponseDTO;
import io.harman.flight_be.dto.coupon.CreateCouponRequestDTO;
import io.harman.flight_be.dto.coupon.UpdateCouponRequestDTO;

public interface CouponService {
    List<CouponResponseDTO> getCoupons();
    CouponResponseDTO getCoupon(UUID id);
    CouponResponseDTO createCoupon(CreateCouponRequestDTO request);
    CouponResponseDTO updateCoupon(UUID id, UpdateCouponRequestDTO request);
    void deleteCoupon(UUID id);
}
