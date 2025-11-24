package io.harman.flight_be.controller;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.harman.flight_be.dto.coupon.CouponResponseDTO;
import io.harman.flight_be.dto.loyalty.AddPointsRequestDTO;
import io.harman.flight_be.dto.loyalty.LoyaltyDashboardResponseDTO;
import io.harman.flight_be.dto.loyalty.LoyaltyPointsResponseDTO;
import io.harman.flight_be.dto.loyalty.PurchaseCouponRequestDTO;
import io.harman.flight_be.dto.loyalty.PurchasedCouponResponseDTO;
import io.harman.flight_be.dto.loyalty.RedeemCouponRequestDTO;
import io.harman.flight_be.dto.loyalty.RedeemCouponResponseDTO;
import io.harman.flight_be.dto.rest.BaseResponseDTO;
import io.harman.flight_be.service.CouponService;
import io.harman.flight_be.service.LoyaltyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/loyalty")
@RequiredArgsConstructor
public class LoyaltyRestController {

    private final LoyaltyService loyaltyService;
    private final CouponService couponService;

    @GetMapping("/coupons/available")
    public ResponseEntity<BaseResponseDTO<List<CouponResponseDTO>>> getAvailableCoupons() {
        var baseResponse = new BaseResponseDTO<List<CouponResponseDTO>>();
        baseResponse.setStatus(HttpStatus.OK.value());
        baseResponse.setData(couponService.getCoupons());
        baseResponse.setMessage("Available coupons retrieved successfully");
        baseResponse.setTimestamp(new Date());
        return new ResponseEntity<>(baseResponse, HttpStatus.OK);
    }

    @PostMapping("/points")
        public ResponseEntity<BaseResponseDTO<LoyaltyPointsResponseDTO>> addPoints(
            @Valid @RequestBody AddPointsRequestDTO request,
            BindingResult bindingResult) {
        var baseResponse = new BaseResponseDTO<LoyaltyPointsResponseDTO>();

        if (bindingResult.hasErrors()) {
            baseResponse.setStatus(HttpStatus.BAD_REQUEST.value());
            baseResponse.setMessage(buildValidationMessage(bindingResult));
            baseResponse.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponse, HttpStatus.BAD_REQUEST);
        }

        LoyaltyPointsResponseDTO responseDTO = loyaltyService.addPoints(request);
        baseResponse.setStatus(HttpStatus.OK.value());
        baseResponse.setData(responseDTO);
        baseResponse.setMessage("Points added successfully");
        baseResponse.setTimestamp(new Date());
        return new ResponseEntity<>(baseResponse, HttpStatus.OK);
    }

    @GetMapping("/customers/{customerId}/balance")
    public ResponseEntity<BaseResponseDTO<LoyaltyPointsResponseDTO>> getBalance(@PathVariable UUID customerId) {
        var baseResponse = new BaseResponseDTO<LoyaltyPointsResponseDTO>();
        LoyaltyPointsResponseDTO responseDTO = loyaltyService.getBalance(customerId);
        baseResponse.setStatus(HttpStatus.OK.value());
        baseResponse.setData(responseDTO);
        baseResponse.setMessage("Balance retrieved successfully");
        baseResponse.setTimestamp(new Date());
        return new ResponseEntity<>(baseResponse, HttpStatus.OK);
    }

    @GetMapping("/customers/{customerId}/dashboard")
    public ResponseEntity<BaseResponseDTO<LoyaltyDashboardResponseDTO>> getDashboard(@PathVariable UUID customerId) {
        var baseResponse = new BaseResponseDTO<LoyaltyDashboardResponseDTO>();
        LoyaltyDashboardResponseDTO responseDTO = loyaltyService.getDashboard(customerId);
        baseResponse.setStatus(HttpStatus.OK.value());
        baseResponse.setData(responseDTO);
        baseResponse.setMessage("Dashboard retrieved successfully");
        baseResponse.setTimestamp(new Date());
        return new ResponseEntity<>(baseResponse, HttpStatus.OK);
    }

    @PostMapping("/coupons/purchase")
    public ResponseEntity<BaseResponseDTO<PurchasedCouponResponseDTO>> purchaseCoupon(
            @Valid @RequestBody PurchaseCouponRequestDTO request,
            BindingResult bindingResult) {
        var baseResponse = new BaseResponseDTO<PurchasedCouponResponseDTO>();

        if (bindingResult.hasErrors()) {
            baseResponse.setStatus(HttpStatus.BAD_REQUEST.value());
            baseResponse.setMessage(buildValidationMessage(bindingResult));
            baseResponse.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponse, HttpStatus.BAD_REQUEST);
        }

        try {
            PurchasedCouponResponseDTO responseDTO = loyaltyService.purchaseCoupon(request);
            baseResponse.setStatus(HttpStatus.CREATED.value());
            baseResponse.setData(responseDTO);
            baseResponse.setMessage("Coupon purchased successfully");
            baseResponse.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponse, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            baseResponse.setStatus(HttpStatus.NOT_FOUND.value());
            baseResponse.setMessage(e.getMessage());
            baseResponse.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponse, HttpStatus.NOT_FOUND);
        } catch (IllegalStateException e) {
            baseResponse.setStatus(HttpStatus.BAD_REQUEST.value());
            baseResponse.setMessage(e.getMessage());
            baseResponse.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponse, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/customers/{customerId}/purchased-coupons")
    public ResponseEntity<BaseResponseDTO<List<PurchasedCouponResponseDTO>>> getPurchasedCoupons(
            @PathVariable UUID customerId) {
        var baseResponse = new BaseResponseDTO<List<PurchasedCouponResponseDTO>>();
        baseResponse.setStatus(HttpStatus.OK.value());
        baseResponse.setData(loyaltyService.getPurchasedCoupons(customerId));
        baseResponse.setMessage("Purchased coupons retrieved successfully");
        baseResponse.setTimestamp(new Date());
        return new ResponseEntity<>(baseResponse, HttpStatus.OK);
    }

    @PostMapping("/coupons/redeem")
        public ResponseEntity<BaseResponseDTO<RedeemCouponResponseDTO>> redeemCoupon(
            @Valid @RequestBody RedeemCouponRequestDTO request,
            BindingResult bindingResult) {
        var baseResponse = new BaseResponseDTO<RedeemCouponResponseDTO>();

        if (bindingResult.hasErrors()) {
            baseResponse.setStatus(HttpStatus.BAD_REQUEST.value());
            baseResponse.setMessage(buildValidationMessage(bindingResult));
            baseResponse.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponse, HttpStatus.BAD_REQUEST);
        }

        try {
            RedeemCouponResponseDTO responseDTO = loyaltyService.redeemCoupon(request);
            baseResponse.setStatus(HttpStatus.OK.value());
            baseResponse.setData(responseDTO);
            baseResponse.setMessage("Coupon redeemed successfully");
            baseResponse.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponse, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            baseResponse.setStatus(HttpStatus.BAD_REQUEST.value());
            baseResponse.setMessage(e.getMessage());
            baseResponse.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponse, HttpStatus.BAD_REQUEST);
        } catch (IllegalStateException e) {
            baseResponse.setStatus(HttpStatus.BAD_REQUEST.value());
            baseResponse.setMessage(e.getMessage());
            baseResponse.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponse, HttpStatus.BAD_REQUEST);
        }
    }

    private String buildValidationMessage(BindingResult bindingResult) {
        StringBuilder builder = new StringBuilder();
        for (FieldError error : bindingResult.getFieldErrors()) {
            builder.append(error.getDefaultMessage()).append("; ");
        }
        return builder.toString();
    }
}
