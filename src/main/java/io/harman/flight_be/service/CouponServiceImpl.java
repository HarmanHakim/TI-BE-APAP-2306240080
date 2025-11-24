package io.harman.flight_be.service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import io.harman.flight_be.dto.coupon.CouponResponseDTO;
import io.harman.flight_be.dto.coupon.CreateCouponRequestDTO;
import io.harman.flight_be.dto.coupon.UpdateCouponRequestDTO;
import io.harman.flight_be.model.loyalty.Coupon;
import io.harman.flight_be.repository.loyalty.CouponRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CouponServiceImpl implements CouponService {

    private final CouponRepository couponRepository;

    public List<CouponResponseDTO> getCoupons() {
        return couponRepository.findAllByOrderByCreatedDateDesc().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public CouponResponseDTO getCoupon(UUID id) {
        Coupon coupon = couponRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Coupon with id " + id + " not found"));
        return mapToResponse(coupon);
    }

    public CouponResponseDTO createCoupon(CreateCouponRequestDTO request) {
        Coupon coupon = Coupon.builder()
                .name(request.getName())
                .description(request.getDescription())
                .points(request.getPoints())
                .percentOff(request.getPercentOff())
                .build();

        Coupon saved = couponRepository.save(coupon);
        return mapToResponse(saved);
    }

    public CouponResponseDTO updateCoupon(UUID id, UpdateCouponRequestDTO request) {
        Coupon coupon = couponRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Coupon with id " + id + " not found"));

        coupon.setName(request.getName());
        coupon.setDescription(request.getDescription());
        coupon.setPoints(request.getPoints());
        coupon.setPercentOff(request.getPercentOff());

        Coupon saved = couponRepository.save(coupon);
        return mapToResponse(saved);
    }

    public void deleteCoupon(UUID id) {
        if (!couponRepository.existsById(id)) {
            throw new IllegalArgumentException("Coupon with id " + id + " not found");
        }
        couponRepository.deleteById(id);
    }

    private CouponResponseDTO mapToResponse(Coupon coupon) {
        return CouponResponseDTO.builder()
                .id(coupon.getId())
                .name(coupon.getName())
                .description(coupon.getDescription())
                .points(coupon.getPoints())
                .percentOff(coupon.getPercentOff())
                .createdDate(coupon.getCreatedDate())
                .updatedDate(coupon.getUpdatedDate())
                .build();
    }
}
